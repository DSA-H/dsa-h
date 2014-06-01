package sepm.dsa.dao;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Unit;

@Repository
@Transactional(readOnly = true)
public class UnitDaoHbmImpl
	extends BaseDaoHbmImpl<Unit>
	implements UnitDao {
}
