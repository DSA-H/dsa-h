package sepm.dsa.service;

import org.springframework.stereotype.Service;
import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.DSADate;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Trader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;

public class TimeServiceImpl implements TimeService {
    static private final float PRODUCT_TURNOVER_PERCENT_PER_DAY = 1.5f;   // trader changes x% of his products per day

    private TraderService traderService;

    private DSADate date;
    private Properties properties;

    public TimeServiceImpl() {
        try {
            properties = new Properties();
            Path path = Paths.get("properties");
            if(!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.exists(path);
            InputStream is = Files.newInputStream(path);
            properties.load(is);
            properties.get("time");
            is.close();
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
     * @param days
     */
    @Override
    public void forwardTime(int days) {
        // save new time
        date.setTimestamp(date.getTimestamp() + days);
        setCurrentDate(date);

        // change sortiment for all traders
        for(Trader trader : traderService.getAll()) {
            int newOffersCount = (int)(PRODUCT_TURNOVER_PERCENT_PER_DAY*trader.getSize()*days);
            int actOffersCount = 0;
            for(Offer offer : trader.getOffers()) {
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
                        containing = true;
                        break;
                    }
                }
                // if offer nt exits, add it
                if(!containing) {
                    trader.getOffers().add(newOffer);
                }
            }
            // persist offers
            traderService.update(trader);
        }


    }

    public void setTraderService(TraderService traderService) {
        this.traderService = traderService;
    }
}
