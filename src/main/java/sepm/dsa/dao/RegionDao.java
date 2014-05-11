package sepm.dsa.dao;

import sepm.dsa.model.Region;

import java.util.List;

public interface RegionDao {

	public int add(Region region);
	public void update(Region region);
	public void remove(Region region);

	public Region get(int id);
	public List<Region> getAll();
}
