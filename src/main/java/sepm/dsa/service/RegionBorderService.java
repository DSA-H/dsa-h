package sepm.dsa.service;

import sepm.dsa.exceptions.DSARuntimeException;
import sepm.dsa.model.Region;
import sepm.dsa.model.RegionBorder;

import java.util.List;

public interface RegionBorderService {

    /**
     * Persists the regionBorder in te database. Bean Validation on Modelclass is performed
     *
     * @param regionBorder must not be null
     * @return The added regionBorder model.
     */
    RegionBorder add(RegionBorder regionBorder);

    /**
     * Updates existing {@code regionBorder} in te Database
     *
     * @param regionBorder must not be null
     * @return The updated regionBorder model.
     */
    RegionBorder update(RegionBorder regionBorder);

    /**
     * Deletes the handed {@code regionBorder} permanently from the database
     *
     * @param regionBorder must not be null
     */
    void remove(RegionBorder regionBorder);

    /**
     * Gives you all the {@code RegionBorder} from the database
     *
     * @return list of all {@code RegionBorder} -- or empty list if nothing found
     */
    List<RegionBorder> getAll();

    /**
     * Gives you all the {@code RegionBorder} from the database for a given {@code regionId}
     *
     * @param regionId the Id for a region - if region does NOT exist the exception is thrown
     * @return
     * @throws sepm.dsa.exceptions.DSARuntimeException
     */
    List<RegionBorder> getAllByRegion(int regionId) throws DSARuntimeException;

    /**
     * Gets the RegionBorder between region1 and region2
     *
     * @param region1
     * @param region2
     * @return the RegionBorder, or null, if no such border exists between region1 and region2
     */
    RegionBorder get(Region region1, Region region2);

}
