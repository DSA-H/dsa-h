package sepm.dsa.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.ProductUnit;

@Repository
@Transactional(readOnly = true)
public class ProductUnitDaoHbmImpl
	extends BaseDaoHbmImpl<ProductUnit>
	implements ProductUnitDao {
}
