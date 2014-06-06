package sepm.dsa.service;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sepm.dsa.dao.OfferDao;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.FileHandler;

public class TimeServiceImpl implements TimeService {
    private static final Logger log = LoggerFactory.getLogger(TimeServiceImpl.class);

    static private final float PRODUCT_TURNOVER_PERCENT_PER_DAY = 1.5f;   // trader changes x% of his products per day

    private TraderService traderService;
    private OfferDao offerDao;
    private LocationService locationService;
    private TavernService tavernService;
	private SaveCancelService saveCancelService;
	private MapService mapService;

    private DSADate date;
    private Properties properties;

    public TimeServiceImpl() {
        try {
            properties = new Properties();
            Path path = Paths.get("properties");
            if(!Files.exists(path)) {
                Files.createFile(path);
            }
            InputStream is = Files.newInputStream(path);
            properties.load(is);
            long timestamp = Long.parseLong(properties.getProperty("time", "0"));
            is.close();
            date = new DSADate(timestamp);
        } catch (IOException e) {
            throw new DSARuntimeException("Probleme beim Laden der Properties Datei! \n" + e.getMessage());
        }
    }

    @Override
    public DSADate getCurrentDate() {
        return date;
    }

    @Override
    public void setCurrentDate(DSADate dsaDate) {
        log.debug("calling setCurrentDate(" + dsaDate + ")");
        try {
            properties.put("time", dsaDate.getTimestamp()+"");
            OutputStream os = Files.newOutputStream(Paths.get("properties"));
            properties.store(os, "");
            os.close();
            date = dsaDate;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Forward time at the given days. Changes offers of all traders (depended on the number of days) and usage of taverns.
     * Moves movingTraders.
     * @param days positiv number
     */
    @Override
    public void forwardTime(int days) {
        log.debug("calling forwardTime(" + days + ")");
        if(days < 1) {
            throw new DSAValidationException("Das Datum muss mindestens einen Tag nach vorne gestellt werden!");
        }
        // save new time
        date.setTimestamp(date.getTimestamp() + days);
        setCurrentDate(date);

        // change sortiment for all traders
        for(Trader trader : traderService.getAll()) {
            int newOffersCount = (int)(PRODUCT_TURNOVER_PERCENT_PER_DAY*trader.getSize()*days);
            if(newOffersCount > trader.getSize()) {
                newOffersCount = trader.getSize();
            }
            int actOffersCount = 0;
            Set<Offer> offers = trader.getOffers();
            for(Offer offer : offers) {
                actOffersCount += offer.getAmount();
            }
            // if to much offers sold, make more new offers
            if(trader.getSize()-newOffersCount > actOffersCount) {
                newOffersCount += (trader.getSize()-newOffersCount) - actOffersCount;
            }
            // if not enough offer sold, delete some random offers
            else {
                int deleteOffersCount = actOffersCount - (trader.getSize()-newOffersCount);
                for(int j = 0; j<deleteOffersCount; j++) {
                    int random = (int) (Math.random() * (actOffersCount-j));
                    int i = 0;
                    Offer deleteOffer = null;
                    for (Offer offer : trader.getOffers()) {
                        i += offer.getAmount();
                        if (random <= i) {
                            offer.setAmount(offer.getAmount() - 1);
                            if(offer.getAmount() == 0) {
                                deleteOffer = offer;
                            }
                            break;
                        }
                    }
                    if(deleteOffer != null) {
                        offerDao.remove(deleteOffer);
                        trader.getOffers().remove(deleteOffer);
                    }
                }
            }
            // add new generated offers
            List<Offer> newOffers = traderService.calculateOffers(trader, newOffersCount);
            for(Offer newOffer : newOffers) {
                boolean containing = false;
                // if offer already exist, change amount
                for(Offer offer : trader.getOffers()) {
                    if(newOffer.getProduct().equals(offer.getProduct()) &&
                       newOffer.getQuality().getValue() == offer.getQuality().getValue()) {
                        offer.setAmount(offer.getAmount() + newOffer.getAmount());
                        offerDao.update(offer);
                        containing = true;
                        break;
                    }
                }
                // if offer not exits, add it
                if(!containing) {
                    offerDao.add(newOffer);
                    trader.getOffers().add(newOffer);
                }
            }
        }

        // move moving traders
        List<MovingTrader> movingTraders = traderService.getAllMovingTraders();
        for(MovingTrader movingTrader : movingTraders) {
            long daysSinceMove = date.getTimestamp() - movingTrader.getLastMoved();
            // chance to move 5 days around the average move day
            double moveChance = (float)(daysSinceMove - movingTrader.getAvgStayDays() + 5) / 10f;
            float random = (float)Math.random();
            // move
            if(random < moveChance) {
                Location actLocation = movingTrader.getLocation();

                // possible locations
                List<Location> possibleLocations = null;
                // distance filter
                if(movingTrader.getPreferredDistance() == DistancePreferrence.GLOBAL) {
                    possibleLocations = locationService.getAll();
                }else if(movingTrader.getPreferredDistance() == DistancePreferrence.REGION) {
                    possibleLocations = locationService.getAllByRegion(actLocation.getRegion().getId());
                }
                // TownSize filter
                if (movingTrader.getPreferredTownSize() != null) {
                    List<Location> removeList = new ArrayList<>();
                    for(Location location : possibleLocations) {
	                    // if not preferred town size, than its a 80% chance to remove the town from the possible goals
                        if(location.getSize() != movingTrader.getPreferredTownSize()) {
                            if(Math.random() <= 0.8f) {
                                removeList.add(location);
                            }
                        }
                    }
                    possibleLocations.remove(removeList);
                }
	            // not allowed to move to same location
	            possibleLocations.remove(movingTrader.getLocation());
                // no possible Locations -> not moving
                if(possibleLocations.isEmpty()) {
                    break;
                }
                // take random one of the possible locations
                int randomIndex = (int)(Math.random()*possibleLocations.size());
                Location goalLocation = possibleLocations.get(randomIndex);
                // move trader
                movingTrader.setLocation(goalLocation);
                movingTrader.setLastMoved(date.getTimestamp());
                // calculate new prices
                for(Offer offer : movingTrader.getOffers()) {
                    int price = traderService.calculatePriceForProduct(offer.getProduct(), movingTrader);
                    if(offer.getProduct().getQuality()) {
                        offer.setPricePerUnit((int)(price*offer.getQuality().getQualityPriceFactor()));
                        offerDao.update(offer);
                    }
                }
	            // random position in new location
	            Image image = new Image("file:" + mapService.getLocationMap(movingTrader.getLocation()));
	            System.out.println("("+image.getWidth()+"/"+image.getHeight()+")");
	            if (image != null) {
		            movingTrader.setxPos((int) (Math.random()*image.getWidth()));
		            movingTrader.setyPos((int) (Math.random()*image.getHeight()));
	            } else {
		            movingTrader.setxPos(0);
		            movingTrader.setyPos(0);
	            }
	            System.out.println("("+movingTrader.getxPos()+"/"+movingTrader.getyPos()+")");

                traderService.update(movingTrader);
	            saveCancelService.save();
            }
        }

        // new tavern useage and price calculation
        List<Tavern> taverns = tavernService.getAll();
        for(Tavern tavern : taverns) {
            // random useage  (todo: more sophisticated usage calculation?)
            tavern.setUsage((int)Math.random()*100);
            tavernService.update(tavern);
        }
    }

    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }

    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }

    public void setTavernService(TavernService tavernService) { this.tavernService = tavernService;}

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

	public void setSaveCancelService(SaveCancelService saveCancelService) {
		this.saveCancelService = saveCancelService;
	}

	public void setMapService(MapService mapService) {
		this.mapService = mapService;
	}
}
