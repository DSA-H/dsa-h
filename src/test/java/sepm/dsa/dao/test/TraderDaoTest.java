package sepm.dsa.dao.test;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.LocationDao;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.dao.TraderCategoryDao;
import sepm.dsa.dao.TraderDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.*;

import static org.junit.Assert.*;

public class TraderDaoTest extends AbstractDatabaseTest {

    @Autowired
    private TraderDao traderDao;
    @Autowired
    private LocationDao locationDao;
    @Autowired
    private TraderCategoryDao traderCategoryDao;
    @Autowired
    private ProductDao productDao;

    @Test
    public void add_shouldPersistEntity() {
        Trader trader = new Trader();
        trader.setName("TestTrader1");
        trader.setCharisma(10);
        trader.setIntelligence(11);
        trader.setMut(14);
        trader.setComment("test12345 Kommentar");
        trader.setConvince(15);
        trader.setSize(20);
        Location l = locationDao.get(1);
        trader.setLocation(l);
        trader.setxPos(1);
        trader.setyPos(2);
        TraderCategory tc = traderCategoryDao.get(1);
        tc.setName("tc1");
        trader.setCategory(tc);

        traderDao.add(trader);

        Trader persistedTrader = traderDao.get(trader.getId());
        assertNotNull(persistedTrader);
    }

    @Test
    public void delete_shouldPersistEntity() {
        Trader trader = traderDao.get(1);

        traderDao.remove(trader);

        assertNull(traderDao.get(1));
    }

    @Test
    public void getMovingTrader() {
        Trader trader = traderDao.get(2);
        assertTrue(trader instanceof MovingTrader);
    }

	//TODO was org.hibernate.PropertyValueException
    @Test(expected = NullPointerException.class)
    public void add_incompleteShouldNotPersist() {
        Trader trader = new Trader();
        trader.setName("TestTrader1");
        trader.setCharisma(10);
        trader.setIntelligence(11);
        trader.setMut(14);
        trader.setComment("test12345 Kommentar");
        trader.setConvince(15);

        traderDao.add(trader);

        Trader persistedTrader = traderDao.get(trader.getId());
        assertNull(persistedTrader);
    }

    @Test
    public void update_ShouldPersistEntity() {
        Trader persistedTrader = traderDao.get(2);

        persistedTrader.setComment("xyz123");

        traderDao.update(persistedTrader);

        persistedTrader = traderDao.get(2);

        assertNotNull(persistedTrader);
        assertTrue(persistedTrader.getName().equals("Megatron"));
        assertTrue(persistedTrader.getComment().equals("xyz123"));
    }

    @Test
    @Transactional(readOnly = false)
    public void add_TraderWithOffersShouldPersistEntity() {
        Trader trader = new Trader();
        trader.setName("TestTrader1");
        trader.setCharisma(10);
        trader.setIntelligence(11);
        trader.setMut(14);
        trader.setComment("test12345 Kommentar");
        trader.setConvince(15);
        trader.setSize(20);
        Location l = locationDao.get(1);
        trader.setLocation(l);
        trader.setxPos(1);
        trader.setyPos(2);
        TraderCategory tc = traderCategoryDao.get(1);
        tc.setName("tc1");
        trader.setCategory(tc);

        Offer o1 = new Offer();
        o1.setAmount(10.0);
        o1.setPricePerUnit(170);
        Product p1 = productDao.get(1);
        o1.setProduct(p1);
        o1.setTrader(trader);
        o1.setQuality(ProductQuality.MANGELHAFT);
        trader.getOffers().add(o1);

        traderDao.add(trader);

        trader = traderDao.get(trader.getId());
        assertNotNull(trader);
        assertTrue(trader.getOffers().size() == 1);
    }
}
