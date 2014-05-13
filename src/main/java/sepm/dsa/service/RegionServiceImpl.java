package sepm.dsa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.RegionBorderDao;
import sepm.dsa.dao.RegionDao;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;

import java.io.Serializable;
import java.util.List;


@Service("RegionService")
@Transactional(readOnly = true)
public class RegionServiceImpl implements RegionService, Serializable {
    private static final long serialVersionUID = 7415861483489569621L;

    private RegionDao regionDao;
    private RegionBorderDao regionBorderDao;

    @Override
    public Region get(int id) {
        return regionDao.get(id);
    }

    @Override
    @Transactional(readOnly = false)
    public int add(Region r) {
        return regionDao.add(r);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Region r) {
        regionDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Region r) {
        List<RegionBorder> borders = regionBorderDao.getAllForRegion(r.getId());
        for (RegionBorder border : borders) {
            regionBorderDao.remove(border);
        }
        regionDao.remove(r);
    }

    @Override
    public List<Region> getAll() {
        return regionDao.getAll();
    }

    public void setRegionDao(RegionDao regionDao) {
        this.regionDao = regionDao;
    }

    public void setRegionBorderDao(RegionBorderDao regionBorderDao) {
        this.regionBorderDao = regionBorderDao;
    }

}
