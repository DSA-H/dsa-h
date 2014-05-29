package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.RegionBorder;

import java.util.List;
import java.util.Vector;

@Transactional(readOnly = true)
public class RegionBorderDaoHbmImpl
	extends BaseDaoHbmImpl<RegionBorder>
	implements RegionBorderDao {

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
