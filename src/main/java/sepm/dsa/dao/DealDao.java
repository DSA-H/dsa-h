package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Deal;

@Transactional(readOnly = true)
public interface DealDao extends BaseDao<Deal> {
}
