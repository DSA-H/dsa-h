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

        // this should work if a transient model (not in the persistence context) is the parameter
//        ProductCategory p = (ProductCategory) sessionFactory.getCurrentSession().load(ProductCategory.class, model.getId());
//        if (p.getParent() != null) {
//            p.getParent().getChilds().remove(model);
//        }
//        if (model.getParent() != null) {
//            ProductCategory newParent = (ProductCategory) sessionFactory.getCurrentSession().load(ProductCategory.class, model.getParent().getId());
//            newParent.getChilds().add(model);
//        }

        ProductCategory result = super.update(model);

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
    public List<ProductCategory> getAll() {
        log.debug("calling getAll()");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("ProductCategory.findAll");
        List<?> list = query.list();
        List<ProductCategory> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((ProductCategory) o);
        }

        log.trace("returning " + result);
        return result;
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

    @Override
    public List<ProductCategory> getAllByName(String name) {
        log.debug("calling getAllByParent(" + name + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("ProductCategory.findAllByName");
        query.setParameter("name", name);
        List<?> list = query.list();
        List<ProductCategory> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((ProductCategory) o);
        }

        log.trace("returning " + result);
        return result;
    }
}
