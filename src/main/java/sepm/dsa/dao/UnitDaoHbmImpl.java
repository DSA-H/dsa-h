package sepm.dsa.dao;


import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Location;
import sepm.dsa.model.Unit;
import sepm.dsa.model.UnitType;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class UnitDaoHbmImpl
	extends BaseDaoHbmImpl<Unit>
	implements UnitDao {

    @Override
    public List<Unit> getAllByType(UnitType unitType) {
        log.debug("calling getAllByType(" + unitType + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Unit.findAllByType");
        query.setParameter("unitTypeId", unitType == null ? null : unitType.getId());
        List<?> list = query.list();

        List<Unit> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Unit) o);
        }

        log.trace("returning " + result);
        return result;
    }

}
