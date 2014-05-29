package sepm.dsa.dao;

import sepm.dsa.model.RegionBorder;

import java.util.List;


public interface RegionBorderDao {

    /**
     * Adds the region to the database
     *
     * @param regionBorder to be persisted must not be null
     */
    void add(RegionBorder regionBorder);

    /**
     * Updates a existing {@code Region}
     *
     * @param regionBorder must exist in the database, must not be null
     */
    void update(RegionBorder regionBorder);

    /**
     * Permanently delets a region from the database
     *
     * @param regionBorder to be deleted, must not be null
     */
    void remove(RegionBorder regionBorder);

    /**
     * Hands you a list of all regions in the database.
     *
     * @return a List of Regions -- if nothing in database empty list is returned
     */
    List<RegionBorder> getAll();

    /**
     * Hands you a list of all regions in the database.
     *
     * @param regionId
     * @return
     */
    List<RegionBorder> getAllByRegion(int regionId);

}
