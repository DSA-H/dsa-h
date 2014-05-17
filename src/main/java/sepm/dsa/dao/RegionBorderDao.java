package sepm.dsa.dao;

import sepm.dsa.model.RegionBorder;

import java.util.List;


public interface RegionBorderDao {

    void add(RegionBorder regionBorder);

    void update(RegionBorder regionBorder);

    void remove(RegionBorder regionBorder);

    List<RegionBorder> getAll();

    List<RegionBorder> getAllByRegion(int regionId);

}
