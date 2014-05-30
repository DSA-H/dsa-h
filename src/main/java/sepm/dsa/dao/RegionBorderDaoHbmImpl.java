package sepm.dsa.dao;

import org.hibernate.Query;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.RegionBorder;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class RegionBorderDaoHbmImpl
	extends BaseDaoHbmImpl<RegionBorder>
	implements RegionBorderDao {

    @Transactional(readOnly = false)
    @Override
    public void remove(RegionBorder regionBorder) {
        log.debug("calling delete(" + regionBorder + ")");
        RegionBorder trueRegionBorder = get(regionBorder.getPk());    //
        sessionFactory.getCurrentSession().delete(trueRegionBorder);
    }

    @Override
    public RegionBorder get(Serializable id) {
        log.debug("calling get(" + id + ")");
        RegionBorder.Pk pk = (RegionBorder.Pk) id;
        Query query = sessionFactory.getCurrentSession().getNamedQuery("RegionBorder.findByRegions");
        query.setParameter("region1ID", pk.getRegion1().getId());
        query.setParameter("region2ID", pk.getRegion2().getId());
        List<?> list = query.list();

        List<RegionBorder> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((RegionBorder) o);
        }
        if (result.size() > 1) {
            log.warn("INCONSISTENT DATA! More than 1 connections between locations " + pk.getRegion1() + " and " + pk.getRegion2());
        }
        if (result.size() == 0) {
            log.trace("returning null");
            return null;
        }
        log.trace("returning " + result);
        return result.get(0);
    }


    @Override
    public List<RegionBorder> getAllByRegion(int regionId) {
        log.debug("calling getAllForRegion(" + regionId + ")");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("RegionBorder.findAllForRegion")
                .setParameter("regionId", regionId)
                .list();

        List<RegionBorder> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((RegionBorder) o);
        }

        log.trace("returning " + result);
        return result;
    }
}
