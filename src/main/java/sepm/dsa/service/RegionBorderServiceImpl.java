package sepm.dsa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.RegionBorderDao;
import sepm.dsa.model.RegionBorder;
import sepm.dsa.model.RegionBorderPk;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Michael on 11.05.2014.
 */
@Service("RegionBorderService")
@Transactional(readOnly = true)
public class RegionBorderServiceImpl implements RegionBorderService, Serializable {

    private static final long serialVersionUID = 7415861483489569621L;

    @Autowired
    private RegionBorderDao regionBorderDao;

    @Override
    @Transactional(readOnly = false)
    public RegionBorderPk add(RegionBorder regionBorder) {
        return regionBorderDao.add(regionBorder);
    }

    @Override
    @Transactional(readOnly = false)
    public void update(RegionBorder regionBorder) {
        regionBorderDao.update(regionBorder);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(RegionBorder regionBorder) {
        regionBorderDao.remove(regionBorder);
    }

    @Override
    public RegionBorder get(RegionBorderPk pk) {
        return regionBorderDao.get(pk);
    }

    @Override
    public List<RegionBorder> getAll() {
        return regionBorderDao.getAll();
    }

    @Override
    public List<RegionBorder> getAllForRegion(int regionId) {
        return regionBorderDao.getAllForRegion(regionId);
    }

    public void setRegionBorderDao(RegionBorderDao regionBorderDao) {
        this.regionBorderDao = regionBorderDao;
    }

}
