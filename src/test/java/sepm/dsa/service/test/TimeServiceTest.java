package sepm.dsa.service.test;


import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.OfferDao;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.DSADate;
import sepm.dsa.model.Location;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Trader;
import sepm.dsa.service.TimeService;
import sepm.dsa.service.TraderService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimeServiceTest extends AbstractDatabaseTest {

    @Autowired
    private TimeService timeService;
    @Autowired
    private TraderDao traderDao;
    @Autowired
    private OfferDao offerDao;
    @Autowired
    private TraderService traderService;

    @Before
    public void befor() {
        // init trader 1 with calculated offers
        Trader trader = traderDao.get(1);

        for(Offer offer : trader.getOffers()) {
            offerDao.remove(offer);
        }
        trader.getOffers().clear();

        List<Offer> offers = traderService.calculateOffers(trader);
        offerDao.addList(offers);
        trader.setOffers(new HashSet<>(offers));
    }

    @Test
    public void changeDateTest() {
        DSADate dsaDate = new DSADate(17);

        timeService.setCurrentDate(dsaDate);
        dsaDate = timeService.getCurrentDate();

        Assert.assertEquals(dsaDate.getTimestamp(), 17);
    }

    @Test
    public void forwardTimeTestmovingTrader() {
        Trader trader = traderDao.get(2);
        Location oldLocation = trader.getLocation();

        timeService.forwardTime(100);

        trader = traderDao.get(2);
        Location newLocation = trader.getLocation();

        Assert.assertTrue(!oldLocation.equals(newLocation));
    }

    @Test
    public void forwardTimeTestChangeSortiment1() {
        Trader trader = traderDao.get(1);

        timeService.forwardTime(10);

        int actAmount = 0;
        Set<Offer> offers = trader.getOffers();
        for(Offer offer : offers) {
            actAmount += offer.getAmount();
        }

        Assert.assertTrue(actAmount == trader.getSize());
    }

    @Test
    public void forwardTimeTestChangeSortiment2() {
        Trader trader = traderDao.get(1);
        // delete a offer
        Offer first = trader.getOffers().stream().findFirst().get();
        trader.getOffers().remove(first);
        traderDao.update(trader);

        int actAmount = 0;
        Set<Offer> offers = trader.getOffers();
        for(Offer offer : offers) {
            actAmount += offer.getAmount();
        }
        Assert.assertTrue(actAmount < trader.getSize());

        timeService.forwardTime(1);

        actAmount = 0;
        offers = trader.getOffers();
        for(Offer offer : offers) {
            actAmount += offer.getAmount();
        }
        Assert.assertTrue(actAmount == trader.getSize());
    }

    @Test
    public void forwardTimeTestChangeSortiment3() {
        Trader trader = traderDao.get(1);
        // edit a offer
        Offer first = trader.getOffers().stream().findFirst().get();
        first.setAmount(first.getAmount() + 10);
        offerDao.update(first);

        saveCancelService.save();

        int actAmount = 0;
        Set<Offer> offers = trader.getOffers();
        for(Offer offer : offers) {
            actAmount += offer.getAmount();
        }
        actAmount -= 10;
        Assert.assertTrue(actAmount == trader.getSize());

        timeService.forwardTime(29);


        actAmount = 0;
        offers = trader.getOffers();
        for(Offer offer : offers) {
            actAmount += offer.getAmount();
        }
        Assert.assertTrue(actAmount == trader.getSize());
    }
}
