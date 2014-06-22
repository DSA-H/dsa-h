package sepm.dsa.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.dao.DealDao;
import sepm.dsa.dbunit.AbstractDatabaseTest;
import sepm.dsa.model.DSADate;
import sepm.dsa.model.Deal;
import sepm.dsa.model.Player;
import sepm.dsa.model.Trader;
import sepm.dsa.service.*;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DealServiceTest extends AbstractDatabaseTest {

//    @Autowired
    private DealService dealService;

    @Autowired
    private DealDao dealDao;

    @Autowired
    private PlayerService playerService;
    @Autowired
    private TraderService traderService;
    private TimeService timeServiceMock;

    public DealServiceTest() {
    }

    @Before
    public void setUp() {
        timeServiceMock = mock(TimeService.class);

        when(timeServiceMock.getCurrentDate()).thenReturn(new DSADate(450));

        DealServiceImpl dealServiceImpl = new DealServiceImpl();
        dealServiceImpl.setTimeService(timeServiceMock);
        dealServiceImpl.setDealDao(dealDao);
        dealService = dealServiceImpl;
    }

    @Test
    public void getAllBetweenPlayerAndTraderLastXDays_returnsCorrectEntries() {
        Player player = playerService.get(1);
        Trader trader = traderService.get(2);
        Deal deal1 = dealService.get(2);
        Deal deal2 = dealService.get(3);
        Deal deal3 = dealService.get(4);
        Deal deal4 = dealService.get(5);
        Set<Deal> expectedDeals = new HashSet<>();
        expectedDeals.add(deal1);
        expectedDeals.add(deal2);
        expectedDeals.add(deal3);
        expectedDeals.add(deal4);
        Set<Deal> deals = new HashSet<>(dealService.getAllBetweenPlayerAndTraderLastXDays(player, trader, 365));

        assertEquals(expectedDeals, deals);
    }


}
