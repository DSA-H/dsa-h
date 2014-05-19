package sepm.dsa.service;

import sepm.dsa.model.Location;
import sepm.dsa.model.Trader;
import sepm.dsa.model.TraderCategory;

import java.util.List;

/**
 * Created by Jotschi on 18.05.2014.
 */
public interface TraderCategoryService {

    TraderCategory get(int id);
    void add(TraderCategory t);
    void update(TraderCategory t);
    void remove(TraderCategory t);
    List<TraderCategory> getAll();
}
