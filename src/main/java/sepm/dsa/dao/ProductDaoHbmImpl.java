package sepm.dsa.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.model.Location;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;

import java.util.List;
import java.util.Vector;

@Repository
@Transactional(readOnly = true)
public class ProductDaoHbmImpl
	extends BaseDaoHbmImpl<Product>
	implements ProductDao {

    @Override
    public Product add(Product model) {
        Product result = super.add(model);

        return result;
    }

    @Override
    public void remove(Product model) {
        super.remove(model);

    }

    @Override
    public List<Product> getAllByCategoryPlusChildren(ProductCategory productCategory) {
        log.debug("calling getAllByCategoryPlusChildren(" + productCategory + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Product.findAllByCategoryPlusChildren");
        query.setParameter("categoryId", productCategory.getId());
        List<?> list = query.list();

        List<Product> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Product) o);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Product> getAllByName(String name) {
        log.debug("calling getAllByName(" + name + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Product.findAllByName");
        query.setParameter("name", name);
        List<?> list = query.list();

        List<Product> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Product) o);
        }

        log.trace("returning " + result);
        return result;
    }

}
