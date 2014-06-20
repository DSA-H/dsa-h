package sepm.dsa.dao;

import sepm.dsa.model.Location;
import sepm.dsa.model.MovingTrader;
import sepm.dsa.model.TraderCategory;

import java.util.List;

public interface MovingTraderDao extends BaseDao<MovingTrader> {
	/**
	 * Finds all MovingTraders for a location
	 *
	 * @return the traders or empty list of no traders exist (not null)
	 */
	public List<MovingTrader> getAllByLocation(Location location);

	/**
	 * Get all moving traders for a specified category.
	 *
	 * @param category the filter string (TraderCategory name), must not be null
	 * @return the list of MovingTraders matching this category or empty list if nothing found
	 */
	public List<MovingTrader> getAllByCategory(TraderCategory category);

    /**
     * Makes a normal Trader to a MovingTrader
     * @param movingTrader
     */
    void addMovingToTrader(MovingTrader movingTrader);

    /**
     * Makes a MovingTrader to a normal Trader
     * @param movingTrader
     */
    void removeMovingFromMovingTrader(MovingTrader movingTrader);

}
