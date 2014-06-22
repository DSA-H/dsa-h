package sepm.dsa.dao;

import org.hibernate.Query;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;

import java.util.List;
import java.util.Vector;

public class ProductDaoHbmImpl
	extends BaseDaoHbmImpl<Product>
	implements ProductDao {

    @Override
    public Product add(Product model) {
        Product result = super.add(model);
        for (ProductCategory c : model.getCategories()) {
            c.getProducts().add(result);
        }
        return result;
    }

    @Override
    public void remove(Product model) {
        super.remove(model);
        for (ProductCategory c : model.getCategories()) {
            c.getProducts().remove(model);
        }
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

    @Override
    public List<Product> getAllByRegion(Region region) {
        log.debug("calling getAllByRegion(" + region + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Product.findAllByRegion");
        query.setParameter("regionId", region == null ? null : region.getId());
        List<?> list = query.list();

        List<Product> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Product) o);
        }

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Product> getAllByRegionName(String regionName) {
        log.debug("calling getAllByRegion(" + regionName + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Product.findAllByRegionName");
        query.setParameter("regionName", regionName);
        List<?> list = query.list();

        List<Product> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Product) o);
        }

        log.trace("returning " + result);
        return result;
    }

}
