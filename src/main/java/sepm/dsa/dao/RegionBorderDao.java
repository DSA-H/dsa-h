package sepm.dsa.dao;

import sepm.dsa.model.RegionBorder;

import java.util.List;


public interface RegionBorderDao extends BaseDao<RegionBorder> {

    /**
     * @param regionId
     * @return all RegionBorders by a specific Region, might be an empty list (not null)
     */
    List<RegionBorder> getAllByRegion(int regionId);
}
