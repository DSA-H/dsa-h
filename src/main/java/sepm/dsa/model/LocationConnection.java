package sepm.dsa.model;

import java.io.Serializable;

public class LocationConnection implements Serializable {

    private static final long serialVersionUID = 5915927914933432772L;

    private Location location1;
    private Location location2;
    private Integer travelTime;
    private String comment;

    public Location getLocation1() {
        return location1;
    }

    public void setLocation1(Location location1) {
        this.location1 = location1;
    }

    public Location getLocation2() {
        return location2;
    }

    public void setLocation2(Location location2) {
        this.location2 = location2;
    }

    public Integer getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



}
