package sepm.dsa.service;

import sepm.dsa.model.Border;
import sepm.dsa.model.Region;

import java.util.List;

public interface BorderService {
    public int add(Border b);
    void update(Border b);
    void remove(Border b);
    /**
     * Get all Borders from Region r
     * @param r Region
     * @return List of Borders
     */
    List<Border> getAllFromRegion(Region r);
}
