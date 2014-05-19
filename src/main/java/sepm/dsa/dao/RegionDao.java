package sepm.dsa.dao;

import sepm.dsa.exceptions.DSARegionNotExistingException;
import sepm.dsa.model.Region;

import java.util.List;

public interface RegionDao {

    /**
     * Adds the region to the database
     *
     * @param region to be persisted must not be null
     */
    public void add(Region region);

    /**
     * Updates a existing {@code Region}
     *
     * @param region must exist in the database, must not be null
     */
    public void update(Region region);

    /**
     * Permanently delets a region from the database
     *
     * @param region to be deleted, must not be null
     */
    public void remove(Region region);

    /**
     * Gives you the {@code Region} for the supplied id. If invalid a exception is thrown.
     *
     * @return not null otherwise exception is thrown
     * @throws sepm.dsa.exceptions.DSARegionNotExistingException
     */
    public Region get(int id) throws DSARegionNotExistingException;

    /**
     * Hands you a list of all regions in the database.
     *
     * @return a List of Regions -- if nothing in database empty list is returned
     */
    public List<Region> getAll();
}
