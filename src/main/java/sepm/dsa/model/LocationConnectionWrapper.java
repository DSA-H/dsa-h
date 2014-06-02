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

//        if (locationConnection.getPk() != null
//                && locationConnection.getPk().equals(that.locationConnection.getPk()))
//            return true;

        System.out.print("before normal test... ");
        if (locationConnection != null &&
                locationConnection.getLocation1().equals(that.locationConnection.getLocation1())
                && locationConnection.getLocation2().equals(that.locationConnection.getLocation2())) {
            System.out.println("equal");
            return true;
        }
        System.out.println("not equal (" + locationConnection + " != null \n" +
                "&& " + (locationConnection.getLocation1()) + ".equals(" + (that.locationConnection.getLocation1()) + ")\n" +
                "&&" + (locationConnection.getLocation2()) + ".equals(" + (that.locationConnection.getLocation2()) + ")");

        System.out.print("before reverse test... ");
        // LocationConnections with swapped location1 and location2 are also equal
        if (locationConnection != null &&
                locationConnection.getLocation1().equals(that.locationConnection.getLocation2())
                && locationConnection.getLocation2().equals(that.locationConnection.getLocation1())) {
            System.out.println("equal");
            return true;
        }
        System.out.println("not equal (" + locationConnection + " != null \n" +
                "&& " + locationConnection.getLocation1() + ".equals(" + that.locationConnection.getLocation2() + ")\n" +
                "&&" + locationConnection.getLocation2() + ".equals(" + that.locationConnection.getLocation1() + ")");

        return false;
    }

    @Override
    public int hashCode() {
        // this could do harm, TODO test this whether it corresponds with equals!
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
