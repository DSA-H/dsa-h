package sepm.dsa.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Product;

@Repository
@Transactional(readOnly = true)
public class ProductDaoHbmImpl
	extends BaseDaoHbmImpl<Product>
	implements ProductDao {
}
