package sepm.dsa.dao;

import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;

import java.util.List;

public interface LocationConnectionDao extends BaseDao<LocationConnection> {
    /**
     * @param location
     * @param locationName
     * @return
     */
    public List<LocationConnection> getAllByLocationName(Location location, String locationName);

    /**
     *
     * @param locationId
     * @return all connections of a location
     */
    public List<LocationConnection> getAllByLocation(int locationId);

}
