package sepm.dsa.service;


import sepm.dsa.model.*;

import java.util.List;

public interface TraderService {

    Trader get(int id);
    void add(Trader t);
    void update(Trader t);
    void remove(Trader t);
    List<Trader> getAllForLocation(Location location);
    List<Trader> getAllByCategory(TraderCategory category);

    List<Offer> calculateOffers(Trader trader);
    int calculatePriceForProduct(Product product, Trader trader);
}
