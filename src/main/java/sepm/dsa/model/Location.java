package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "locations")
public class Location implements Serializable {

    private static final long serialVersionUID = 1616654812413948966L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 100, min = 1)
    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name="region", nullable = false)
    private Region region;

    @NotNull
    @Column(nullable = false)
    private Integer xCoord;

    @NotNull
    @Column(nullable = false)
    private Integer yCoord;

    @NotNull
    @Column(nullable = false)
    private Integer sizeId;

    @Column(nullable = true)
    private String planFileName;

    @NotNull
    @Column(nullable = false)
    private Integer height;

    @Column(nullable = true)
    private Integer weatherId;

    @Column(nullable = true)
    @Embedded
    //TODO abklären martin johannes
    private DSADate weatherCollectedDate;

    @Size(max = 1000)
    @Column(nullable = true, length = 1000)
    private String comment;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.location1", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OneToMany
//    @JoinColumn(name = "location1_fk")
    @OneToMany(mappedBy = "pk.location1")
    private Set<LocationConnection> connections1 = new HashSet<>();

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.location2", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OneToMany
//    @JoinColumn(name = "location2_fk")
    @OneToMany(mappedBy = "pk.location2")
    private Set<LocationConnection> connections2 = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getxCoord() {
        return xCoord;
    }

    public void setxCoord(Integer xCoord) {
        this.xCoord = xCoord;
    }

    public Integer getyCoord() {
        return yCoord;
    }

    public void setyCoord(Integer yCoord) {
        this.yCoord = yCoord;
    }

    public TownSize getSize() {
        if (sizeId == null) {
            return null;
        }
        return TownSize.parse(sizeId);
    }

    public void setSize(TownSize size) {
        if (size == null) {
            this.sizeId = null;
        } else {
            this.sizeId = size.getValue();
        }
    }

//    public boolean containsConnection(LocationConnection connection) {
//
//        if (connection.getLocation1().getId().equals(this.getId()))
//
//        if (connections1.contains(connection)) {
//            return true;
//        }
//        if (connections2.contains(connection)) {
//            return true;
//        }
//        return false;
//    }

    public String getPlanFileName() {
        return planFileName;
    }

    public void setPlanFileName(String planFileName) {
        this.planFileName = planFileName;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Weather getWeather() {
        if (weatherId == null) {
            return null;
        }
        return Weather.parse(weatherId);
    }

    public void setWeather(Weather weather) {
        if (weather == null) {
            this.weatherId = null;
        } else {
            this.weatherId = weather.getValue();
        }
    }

    public DSADate getWeatherCollectedDate() {
        return weatherCollectedDate;
    }

    public void setWeatherCollectedDate(DSADate weatherCollectedDate) {
        this.weatherCollectedDate = weatherCollectedDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

//    public Set<LocationConnection> getConnections1() {
//        return connections1;
//    }
//
//    /**
//     *
//     * @param connections1 must not be null
//     */
//    public void setConnections1(Set<LocationConnection> connections1) {
////        this.connections1 = new HashSet<>(connections1);
//        this.connections1 = connections1;
//    }
//
//    public Set<LocationConnection> getConnections2() {
//        return connections2;
//    }
//
//    /**
//     * @param connections2 must not be null
//     */
//    public void setConnections2(Set<LocationConnection> connections2) {
////        this.connections2 = new HashSet<>(connections2);
//        this.connections2 = connections2;
//    }

    public Set<LocationConnection> getAllConnections() {
        Set<LocationConnection> result = new HashSet<>(connections1.size() + connections2.size());
        result.addAll(connections1);
        result.addAll(connections2);
        return result;
    }

//    public LocationConnection addConnection(Location to, LocationConnection connection) {
//        if (to != null) {
//            if (this.id == null || this.id > to.getId()) { //
//                connection.setLocation1(to);
//                connection.setLocation2(this);
//                if (connections2.add(connection)) {
//                    return connection;
//                }
//            } else {
//                connection.setLocation1(this);
//                connection.setLocation2(to);
//                if (connections1.add(connection)) {
//                    return connection;
//                }
//            }
//        } else {
//            if (connections1.add(connection)) {
//                return connection;
//            }
//        }
//        return null;
//    }
//
//    public LocationConnection removeConnection(Location to) {
//        for (LocationConnection l : connections1) {
//            if (equalsByPk(l.getLocation1()) || equalsByPk(l.getLocation2())) {
//                connections1.remove(l);
//                return l;
//            }
//        }
//        for (LocationConnection l : connections2) {
//            if (equalsByPk(l.getLocation1()) || equalsByPk(l.getLocation2())) {
//                connections2.remove(l);
//                return l;
//            }
//        }
//        return null;
//    }
//
//    public LocationConnection removeConnection(LocationConnection connection) {
//        if (connections1.remove(connection)) {
//            return connection;
//        }
//        if (connections2.remove(connection)) {
//            return connection;
//        }
//        return null;
//    }

    public boolean equalsByPk(Location location) {
        if (id == null || location.id == null) {
            return false;
        }
        return id.equals(location.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (comment != null ? !comment.equals(location.comment) : location.comment != null) return false;
        if (height != null ? !height.equals(location.height) : location.height != null) return false;
        if (id != null ? !id.equals(location.id) : location.id != null) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;
        if (planFileName != null ? !planFileName.equals(location.planFileName) : location.planFileName != null)
            return false;
        if (region != null ? !region.equals(location.region) : location.region != null) return false;
        if (sizeId != location.sizeId) return false;
        if (weatherId != location.weatherId) return false;
        if (weatherCollectedDate != null ? !weatherCollectedDate.equals(location.weatherCollectedDate) : location.weatherCollectedDate != null)
            return false;
        if (xCoord != null ? !xCoord.equals(location.xCoord) : location.xCoord != null) return false;
        if (yCoord != null ? !yCoord.equals(location.yCoord) : location.yCoord != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (xCoord != null ? xCoord.hashCode() : 0);
        result = 31 * result + (yCoord != null ? yCoord.hashCode() : 0);
        result = 31 * result + (sizeId != null ? sizeId.hashCode() : 0);
        result = 31 * result + (planFileName != null ? planFileName.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        result = 31 * result + (weatherId != null ? weatherId.hashCode() : 0);
        result = 31 * result + (weatherCollectedDate != null ? weatherCollectedDate.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region=" + region +
                '}';
    }
}
