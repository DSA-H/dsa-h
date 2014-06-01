package sepm.dsa.dao;

import sepm.dsa.model.RegionBorder;

import java.util.List;


public interface RegionBorderDao extends BaseDao<RegionBorder> {
    /**
     * Hands you a list of all regions in the database.
     *
     * @param regionId
     * @return
     */
    List<RegionBorder> getAllByRegion(int regionId);
}
