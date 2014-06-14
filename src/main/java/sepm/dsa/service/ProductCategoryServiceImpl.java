package sepm.dsa.service;

import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sepm.dsa.dao.ProductCategoryDao;
import sepm.dsa.exceptions.DSAValidationException;
import sepm.dsa.model.AssortmentNature;
import sepm.dsa.model.Product;
import sepm.dsa.model.ProductCategory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public class ProductCategoryServiceImpl implements ProductCategoryService {
    private static final Logger log = LoggerFactory.getLogger(RegionServiceImpl.class);
    private Validator validator = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory().getValidator();
    private ProductCategoryDao productCategoryDao;
    private AssortmentNatureService assortmentNatureService;

    @Override
    public ProductCategory get(Integer id) {
        log.debug("calling get(" + id + ")");
        ProductCategory result = productCategoryDao.get(id);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public ProductCategory add(ProductCategory p) {
        log.debug("calling addConnection(" + p + ")");
        validate(p);
        return productCategoryDao.add(p);
    }

    @Override
    @Transactional(readOnly = false)
    public ProductCategory update(ProductCategory p) {
        log.debug("calling update(" + p + ")");
        validate(p);
        return productCategoryDao.update(p);
    }

    @Override
    public List<ProductCategory> getAllRoot() {
        log.debug("calling getAll()");
        List<ProductCategory> result = productCategoryDao.getAllByParent(null);
        log.trace("returning " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = false)
    public void remove(ProductCategory p) {
        log.debug("calling remove(" + p + ")");
        p = get(p.getId());
        for (AssortmentNature a : assortmentNatureService.getAllByProductCategory(p.getId())) {
            assortmentNatureService.remove(a);
        }
        List<ProductCategory> childs = this.getAllChilds(p);
         for (ProductCategory productCategoryChild: childs) {
             productCategoryChild = get(productCategoryChild.getId());
             for (AssortmentNature a : assortmentNatureService.getAllByProductCategory(productCategoryChild.getId())) {
                 assortmentNatureService.remove(a);
             }
        }
        Set<ProductCategory> children = new HashSet<>(p.getChilds());
        children.forEach(this::remove);
        productCategoryDao.remove(p);
        for (Product product : p.getProducts()) {
            product.getCategories().remove(p);
        }
    }

    @Override
    public List<ProductCategory> getAll() {
        log.debug("calling getAll()");
        List<ProductCategory> result = productCategoryDao.getAll();
        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<ProductCategory> getAllChilds(ProductCategory productCategory) {
        log.debug("calling getAllChilds()");
        LinkedList<ProductCategory> result = new LinkedList<>();

        addAllProductCategoryChildren(productCategory, result);

        log.trace("returning " + result);
        return result;
    }

    @Override
    public List<ProductCategory> getAllByName(String name) {
        log.debug("calling getAllByName(" + name + ")");
        List<ProductCategory> result = productCategoryDao.getAllByName(name == null ? null : "%" + name + "%");
        log.trace("returning " + result);
        return result;
    }


    private List<ProductCategory> addAllProductCategoryChildren(ProductCategory productCategory, List<ProductCategory> target) {
        target.add(productCategory);

        for (ProductCategory child : productCategory.getChilds()) {
            addAllProductCategoryChildren(child, target);
        }

        return target;
    }


    /**
     * Validates a product
     *
     * @param product
     * @throws sepm.dsa.exceptions.DSAValidationException if product is not valid
     */
    private void validate(ProductCategory product) throws DSAValidationException {
        Set<ConstraintViolation<ProductCategory>> violations = validator.validate(product);
        if (violations.size() > 0) {
            throw new DSAValidationException("Produktkategorie ist nicht valide.", violations);
        }
    }

    public ProductCategoryDao getProductCategoryDao() {
        return productCategoryDao;
    }

    public void setProductCategoryDao(ProductCategoryDao productCategoryDao) {
        this.productCategoryDao = productCategoryDao;
    }

    public void setAssortmentNatureService(AssortmentNatureService assortmentNatureService) {
        this.assortmentNatureService = assortmentNatureService;
    }
}
