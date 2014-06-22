package sepm.dsa.dao;

import org.hibernate.Query;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Product;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class OfferDaoHbmImpl extends BaseDaoHbmImpl<Offer> implements OfferDao {

    @Override
    public void remove(Offer model) {
        super.remove(model);

        model.getTrader().getOffers().remove(model);
    }

    @Override
    public Offer add(Offer model) {
        Offer result = super.add(model);
        result.getTrader().getOffers().add(result);
        return result;
    }

    @Override
    public void addList(Collection<Offer> offers) {
        for(Offer offer : offers) {
            this.add(offer);
        }
    }

    @Override
    public List<Offer> getAllByProduct(Product product) {
        log.debug("calling getAllByProduct(" + product + ")");
        Query query = sessionFactory.getCurrentSession().getNamedQuery("Offer.findAllByProduct");
        query.setParameter("productId", product == null ? null : product.getId());
        List<?> list = query.list();

        List<Offer> result = new Vector<>(list.size());
        for (Object o : list) {
            result.add((Offer) o);
        }
        log.trace("returning " + result);
        return result;
    }
}
