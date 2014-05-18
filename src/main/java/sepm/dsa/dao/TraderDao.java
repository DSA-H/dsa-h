package sepm.dsa.dao;

import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;

import java.util.List;

/**
 * Created by Jotschi on 18.05.2014.
 */
public interface TraderDao {

    void add(Trader trader);

    void update(Trader trader);

    void remove(Trader trader);

    Trader get(int id);

    List<Location> getAllbyLocation(Location location);
}
