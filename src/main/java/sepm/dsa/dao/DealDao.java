package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Deal;
import sepm.dsa.model.Player;
import sepm.dsa.model.Trader;

import java.util.List;

@Transactional(readOnly = true)
public interface DealDao extends BaseDao<Deal> {

    /**
     * @param player the purchaser
     * @param trader the trader
     * @param fromDate time range start
     * @param toDate time range ending
     * @return all deals between the player and the trader between fromDate and toDate
     */
    List<Deal> playerDealsWithTraderInTimeRange(Player player, Trader trader, long fromDate, long toDate);

}
