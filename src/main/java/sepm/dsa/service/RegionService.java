package sepm.dsa.service;

import sepm.dsa.model.Region;

public interface RegionService {
	public Region get(int id);
	public int add(Region r);
}
