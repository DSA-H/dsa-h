package sepm.dsa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);

    private RegionDao regionDao;
    private RegionBorderDao regionBorderDao;

    @Override
    public Region get(int id) {
        log.debug("calling get(" + id + ")");
        Region result = regionDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public int add(Region r) {
        log.debug("calling add(" + r + ")");
        int result = regionDao.add(r);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public void update(Region r) {
        log.debug("calling update(" + r + ")");
        regionDao.update(r);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Region r) {
        log.debug("calling remove(" + r + ")");
        List<RegionBorder> borders = regionBorderDao.getAllForRegion(r.getId());
        for (RegionBorder border : borders) {
            regionBorderDao.remove(border);
        }
        regionDao.remove(r);
    }

    @Override
    public List<Region> getAll() {
        log.debug("calling getAll()");
        List<Region> result = regionDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    public void setRegionDao(RegionDao regionDao) {
        log.debug("calling setRegionDao(" + regionDao + ")");
        this.regionDao = regionDao;
    }

    public void setRegionBorderDao(RegionBorderDao regionBorderDao) {
        log.debug("calling setRegionBorderDao(" + regionBorderDao + ")");
        this.regionBorderDao = regionBorderDao;
    }

}
