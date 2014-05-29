package sepm.dsa.dao;

import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.TraderCategory;

@Transactional(readOnly = true)
public class TraderCategoryDaoImpl
	extends BaseDaoHbmImpl<TraderCategory>
	implements TraderCategoryDao {
}
