package sepm.dsa.dao;

import sepm.dsa.model.Deal;
import sepm.dsa.model.Player;
import sepm.dsa.model.Product;
import sepm.dsa.model.Trader;

import java.util.List;

public interface DealDao extends BaseDao<Deal> {

    /**
     * @param player the purchaser
     * @param trader the trader
     * @param fromDate time range start
     * @param toDate time range ending
     * @return all deals between the player and the trader between fromDate and toDate
     */
    List<Deal> playerDealsWithTraderInTimeRange(Player player, Trader trader, long fromDate, long toDate);

    /**
     * @param product the Product, not null
     * @return all deals involving this product, might be an empty list (not null)
     */
    List<Deal> getAllByProduct(Product product);

    /**
     * @param player the Player, not null
     * @return all deals with this player involved, might be an empty list (not null)
     */
    List<Deal> getAllByPlayer(Player player);

}
