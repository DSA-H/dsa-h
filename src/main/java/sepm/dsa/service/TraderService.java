package sepm.dsa.service;


import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;

import java.util.List;

public interface TraderService {

    Trader get(int id);
    void add(Trader t);
    void update(Trader t);
    void remove(Trader t);
    List<Trader> getAllForLocation(Location location);
}
