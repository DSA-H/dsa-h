package sepm.dsa.dao;

import sepm.dsa.model.Offer;

import java.util.Collection;
import java.util.List;

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
}
