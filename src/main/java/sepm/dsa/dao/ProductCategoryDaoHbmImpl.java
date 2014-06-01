package sepm.dsa.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.model.ProductCategory;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class ProductCategoryDaoHbmImpl
	extends BaseDaoHbmImpl<ProductCategory>
	implements ProductCategoryDao {

    @Override
    public ProductCategory add(ProductCategory model) {
        ProductCategory result = super.add(model);
        if (model.getParent() != null) {
            result.getParent().getChilds().add(model);
        }
        return result;
    }

    @Override
    public ProductCategory update(ProductCategory model) {
        ProductCategory result = super.update(model);
//        if (model.getParent() != null) {
//            result.getParent().getChilds().add(model);
//        }
//        TODO make correct references of parent.childs if parent changed
        return result;
    }

    @Override
    public void remove(ProductCategory model) {
        super.remove(model);
        if (model.getParent() != null) {
            model.getParent().getChilds().remove(model);
        }
    }

    @Override
    public List<ProductCategory> getAllByParent(ProductCategory parent) {
        log.debug("calling getAllByParent(" + parent + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("ProductCategory.findAllByParent");
        query.setParameter("parentId", parent == null ? null : parent.getId());
        List<?> list = query.list();
        List<ProductCategory> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((ProductCategory) o);
        }

        log.trace("returning " + result);
        return result;
    }
}
