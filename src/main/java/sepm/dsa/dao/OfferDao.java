package sepm.dsa.dao;

import sepm.dsa.model.Location;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Trader;

import java.util.List;

/**
 * Created by Jotschi on 18.05.2014.
 */
public interface OfferDao {

    void add(Offer offer);

    void update(Offer offer);

    void remove(Offer offer);

    Offer get(int id);

    List<Location> getAllbyTrader(Trader trader);
}
