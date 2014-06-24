package sepm.dsa.dao;

import sepm.dsa.model.Tavern;

import java.util.List;

public interface TavernDao extends BaseDao<Tavern> {

    /**
     * @param locationId
     * @return all Taverns in a specific Location, might be an empty list (not null)
     */
    List<Tavern> getAllByLocation(int locationId);

}
