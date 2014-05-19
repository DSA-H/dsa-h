package sepm.dsa.service;


import sepm.dsa.model.Location;
import sepm.dsa.model.Offer;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import java.util.List;

public interface TraderService {

    Trader get(int id);
    void add(Trader t);
    void update(Trader t);
    void remove(Trader t);
    List<Trader> getAllForLocation(Location location);
    List<Trader> getAllByCategory(TraderCategory category);

    List<Offer> calcualteOffers(Trader trader);
}
