package sepm.dsa.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.ProductCategory;

@Repository
@Transactional(readOnly = true)
public class ProductCategoryDaoHbmImpl
	extends BaseDaoHbmImpl<ProductCategory>
	implements ProductCategoryDao {
}
