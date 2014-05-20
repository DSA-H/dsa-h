package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="connections")
public class LocationConnection implements Serializable {

    private static final long serialVersionUID = 5915927914933432772L;

    @EmbeddedId
    private Pk pk = new Pk();

    @NotNull
    @Column(nullable = false)
    private Integer travelTime;

    @Column(nullable = true)
    private String comment;

    public Location getLocation1() {
        return pk.location1;
    }

    public void setLocation1(Location location1) {
        this.pk.location1 = location1;
    }

    public Location getLocation2() {
        return pk.location2;
    }

    public void setLocation2(Location location2) {
        this.pk.location2 = location2;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationConnection that = (LocationConnection) o;

        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (pk != null ? !pk.equals(that.pk) : that.pk != null) return false;
        if (travelTime != null ? !travelTime.equals(that.travelTime) : that.travelTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pk != null ? pk.hashCode() : 0;
        result = 31 * result + (travelTime != null ? travelTime.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocationConnection{" +
                "travelTime=" + travelTime +
                ", pk=" + pk +
                '}';
    }

    @Embeddable
    private static class Pk implements Serializable {

        private static final long serialVersionUID = 5989205421915335466L;

        // TODO BeanValidation for location1 != location2

        @NotNull
        @ManyToOne
        @JoinColumn(nullable = false, insertable=false, updatable=false)
        private Location location1;

        @NotNull
        @ManyToOne
        @JoinColumn(nullable = false, insertable=false, updatable=false)
        private Location location2;

        public Pk() {}

        public Location getLocation2() {
            return location2;
        }

        public void setLocation2(Location location2) {
            this.location2 = location2;
        }

        public Location getLocation1() {
            return location1;
        }

        public void setLocation1(Location location1) {
            this.location1 = location1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pk pk = (Pk) o;

            if (location1 != null ? !location1.equals(pk.location1) : pk.location1 != null) return false;
            if (location2 != null ? !location2.equals(pk.location2) : pk.location2 != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = location1 != null ? location1.hashCode() : 0;
            result = 31 * result + (location2 != null ? location2.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Pk{" +
                    "location1=" + location1 +
                    ", location2=" + location2 +
                    '}';
        }
    }


}
