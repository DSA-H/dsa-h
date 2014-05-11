package sepm.dsa.service;

import sepm.dsa.model.Region;

import java.util.List;

public interface RegionService {
	public Region get(int id);
	public int add(Region r);
    void update(Region r);
    void remove(Region r);
    List<Region> getAll();
}
