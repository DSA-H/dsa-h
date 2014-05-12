package sepm.dsa.dao;

import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.RegionBorderPk;

import java.util.List;

/**
 * Created by Michael on 11.05.2014.
 */
public interface RegionBorderDao {

    RegionBorderPk add(RegionBorder regionBorder);

    void update(RegionBorder regionBorder);

    void remove(RegionBorder regionBorder);

    RegionBorder get(RegionBorderPk pk);

    List<RegionBorder> getAll();

    List<RegionBorder> getAllForRegion(int regionId);

}
