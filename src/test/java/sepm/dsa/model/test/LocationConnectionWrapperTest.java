package sepm.dsa.model.test;

import org.junit.Test;
import sepm.dsa.model.Location;
import sepm.dsa.model.LocationConnection;
import sepm.dsa.model.LocationConnectionWrapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LocationConnectionWrapperTest {

    @Test
    public void equals_swappedLocation1Location2StillEquals() {

        Location location1 = new Location();
        location1.setId(1);
        location1.setName("location1");

        Location location2 = new Location();
        location2.setId(2);
        location2.setName("location2");

        LocationConnection locationConnection1 = new LocationConnection();
        locationConnection1.setLocation1(location1);
        locationConnection1.setLocation2(location2);

        LocationConnection locationConnection2 = new LocationConnection();
        locationConnection2.setLocation1(location2);
        locationConnection2.setLocation2(location1);

        LocationConnectionWrapper wrapper1 = new LocationConnectionWrapper(locationConnection1);
        LocationConnectionWrapper wrapper2 = new LocationConnectionWrapper(locationConnection2);

        assertTrue("wrapper1.equals(wrapper2) must be true!", wrapper1.equals(wrapper2));
        assertEquals(wrapper1.hashCode(), wrapper2.hashCode());
//        assertEquals(wrapper1, wrapper2);
    }

}
