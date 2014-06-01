package sepm.dsa.dao;

import sepm.dsa.model.Offer;
import sepm.dsa.model.Trader;

import java.util.Collection;
import java.util.List;

public interface OfferDao extends BaseDao<Offer> {

    /**
     * Saves a list of offers
     * @param offers
     */
    public void addList(Collection<Offer> offers);

}