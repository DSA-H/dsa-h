package sepm.dsa.service;

import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import java.util.List;

public interface LocationService {

    /**
     * Asks the DAO to persist a {@code Location} in the Database
     *
     * @param location to be persisted must not be null
     * @return
     */
   public void add(Location location);

    /**
     * Asks the DAO to update a already existing {@code Location} in the database
     *
     * @param location to update must not be null
     */
    public void update(Location location);

    /**
     * Asks the DAO to delete a location permanently
     *
     * @param location to be deleted must not be null
     */
    public void remove(Location location);

    Location get(int id);

    /**
     * Asks the DAO to find all Locations
     *
     * @return the locations or empty list of no locations exist (not null)
     */
    public  List<Location> getAll();


}
