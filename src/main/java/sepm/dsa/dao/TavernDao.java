package sepm.dsa.dao;

import sepm.dsa.model.Tavern;

import java.util.List;

public interface TavernDao extends BaseDao<Tavern> {

    List<Tavern> getAllByLocation(int locationId);

}
