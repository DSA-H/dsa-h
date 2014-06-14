package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.OfferDao;
import sepm.dsa.dao.ProductDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;
import sepm.dsa.model.Region;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


@Service("ProductService")
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();
    private ProductDao productDao;
    private ProductCategoryService productCategoryService;
    private OfferDao offerDao;

    @Override
    public Product get(int id) {
        log.debug("calling get(" + id + ")");
        Product result = productDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public Product add(Product p) {
        log.debug("calling addConnection(" + p + ")");
        validate(p);
        return productDao.add(p);
    }

    @Override
    @Transactional(readOnly = false)
    public Product update(Product p) {
        log.debug("calling update(" + p + ")");
        validate(p);
        return productDao.update(p);
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(Product p) {
        log.debug("calling removeConnection(" + p + ")");
        //productDao.removeConnection(get(p.getId()));

        List<Offer> offers = offerDao.getAllByProduct(p);
        offers.forEach(offerDao::remove);

        productDao.remove(p);
    }

    @Override
    public List<Product> getAll() {
        log.debug("calling getAll()");
        List<Product> result = productDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    public Set<Product> getAllFromProductcategory(ProductCategory productCategory) {
        log.debug("calling getAllFromProductcategory");
//        List<Product> result = productDao.getAllByCategoryPlusChildren(productCategory);  // TODO this will be used when solved the (n+1) problem when changing ProductCategory.products to fetch=LAZY again

        LinkedList<ProductCategory> categories = new LinkedList<>();
        int productCount = addAllProductCategoryChildren(productCategory, categories);

        Set<Product> result = new HashSet<Product>(productCount);

        for (ProductCategory c : categories) {
            result.addAll(c.getProducts());
        }
        log.trace("returning " + result);
        return result;
    }

    @Override
    public Set<Product> getBySearchTerm(String searchTerm) {
        log.debug("calling getBySearchTerm(" + searchTerm + ")");
        Set<Product> result = new HashSet<>(productDao.getAllByName(searchTerm == null ? null : "%" + searchTerm + "%"));
        List<ProductCategory> matchingCategories = productCategoryService.getAllByName(searchTerm);
        for (ProductCategory c : matchingCategories) {
            for (Product p : getAllFromProductcategory(c)) {
                result.add(p);
            }
        }
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<Product> getAllByProductionRegion(Region region) {
        log.debug("calling getAllByProductionRegion(" + region + ")");
        List<Product> result = productDao.getAllByRegion(region);
        log.trace("returning " + result);
        return result;
    }

    private int addAllProductCategoryChildren(ProductCategory productCategory, List<ProductCategory> target) {
        int productCount = 0;
        target.add(productCategory);

        productCount += productCategory.getProducts().size();

        for (ProductCategory child : productCategory.getChilds()) {
            productCount += addAllProductCategoryChildren(child, target);
        }

        return productCount;
    }

    public void setProductDao(ProductDao productDao) {
        log.debug("calling setProductDao(" + productDao + ")");
        this.productDao = productDao;
    }

    /**
     * Validates a product
     *
     * @param product
     * @throws sepm.dsa.exceptions.DSAValidationException if product is not valid
     */
    private void validate(Product product) throws DSAValidationException {
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if (violations.size() > 0) {
            throw new DSAValidationException("Produkt ist nicht valide.", violations);
        }
    }

    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    public void setOfferDao(OfferDao offerDao) {
        this.offerDao = offerDao;
    }
}
