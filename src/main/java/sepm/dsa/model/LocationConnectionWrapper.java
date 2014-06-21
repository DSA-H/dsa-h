package sepm.dsa.model;

public class LocationConnectionWrapper {

    private LocationConnection locationConnection;

    public LocationConnectionWrapper(LocationConnection locationConnection) {
        this.locationConnection = locationConnection;
    }

    public LocationConnection getLocationConnection() {
        return locationConnection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationConnectionWrapper that = (LocationConnectionWrapper) o;

        if (locationConnection != null &&
                locationConnection.getLocation1().equals(that.locationConnection.getLocation1())
                && locationConnection.getLocation2().equals(that.locationConnection.getLocation2())) {
            return true;
        }

        // LocationConnections with swapped location1 and location2 are also equal
        if (locationConnection != null &&
                locationConnection.getLocation1().equals(that.locationConnection.getLocation2())
                && locationConnection.getLocation2().equals(that.locationConnection.getLocation1())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash1 = 0;
        if (locationConnection.getPk() != null) {
            hash1 = (locationConnection.getLocation1() != null ? locationConnection.getLocation1().hashCode() : 0);
            hash1 += 31 * (locationConnection.getLocation2() != null ? locationConnection.getLocation2().hashCode() : 0);
        }int hash2 = 0;
        if (locationConnection.getPk() != null) {
            hash2 = (locationConnection.getLocation2() != null ? locationConnection.getLocation2().hashCode() : 0);
            hash2 += 31 * (locationConnection.getLocation1() != null ? locationConnection.getLocation1().hashCode() : 0);
        }
        return hash1 + hash2;
    }
}
