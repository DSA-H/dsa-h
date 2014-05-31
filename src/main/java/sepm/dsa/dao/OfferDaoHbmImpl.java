package sepm.dsa.dao;

import sepm.dsa.model.Offer;

import java.util.Collection;
import java.util.List;

public class OfferDaoHbmImpl extends BaseDaoHbmImpl<Offer> implements OfferDao {
    @Override
    public void addList(Collection<Offer> offers) {
        for(Offer offer : offers) {
            this.add(offer);
        }
    }
}
