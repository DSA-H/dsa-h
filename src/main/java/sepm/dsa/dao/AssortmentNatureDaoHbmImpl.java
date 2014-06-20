package sepm.dsa.dao;

import sepm.dsa.model.AssortmentNature;

import java.util.List;
import java.util.Vector;

public class AssortmentNatureDaoHbmImpl extends BaseDaoHbmImpl<AssortmentNature> implements AssortmentNatureDao {

    @Override
    public AssortmentNature add(AssortmentNature model) {
        AssortmentNature result = super.add(model);
        model.getTraderCategory().putAssortment(model);
        return result;
    }

    @Override
    public AssortmentNature update(AssortmentNature model) {
        AssortmentNature result = super.update(model);
//        model.getTraderCategory().putAssortment(model);
        return result;
    }

    @Override
    public void remove(AssortmentNature model) {
        super.remove(model);
        model.getTraderCategory().removeAssortment(model);
    }


    @Override
    public List<AssortmentNature> getAllByProductCategory(int productCategoryId) {
        log.debug("calling getAllByProductCategory(" + productCategoryId + ")");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("AssortmentNature.findAllByProductCategory")
                .setParameter("productCategoryId", productCategoryId)
                .list();

        List<AssortmentNature> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((AssortmentNature) o);
        }

        log.trace("returning " + result);
        return result;

    }

    @Override
    public List<AssortmentNature> getAllByTraderCategory(int traderCategoryId) {
        log.debug("calling getAllByTraderCategory(" + traderCategoryId + ")");
        List<?> list = sessionFactory.getCurrentSession().getNamedQuery("AssortmentNature.findAllByTraderCategory")
                .setParameter("traderCategoryId", traderCategoryId)
                .list();

        List<AssortmentNature> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((AssortmentNature) o);
        }

        log.trace("returning " + result);
        return result;

    }
}
