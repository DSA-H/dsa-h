package sepm.dsa.dao;

import sepm.dsa.model.RegionBorder;

import java.util.List;


public interface RegionBorderDao extends BaseDao<RegionBorder> {
    List<RegionBorder> getAllByRegion(int regionId);
}
