package sepm.dsa.service;

import sepm.dsa.model.TraderCategory;

import java.util.List;

public interface TraderCategoryService {

    TraderCategory get(int id);
    void add(TraderCategory t);
    void update(TraderCategory t);
    void remove(TraderCategory t);
    List<TraderCategory> getAll();
}
