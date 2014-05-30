package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "locations")
public class Location implements BaseModel {

    private static final long serialVersionUID = 1616654812413948966L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 100, min = 1)
    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne
    @JoinColumn(name = "region", nullable = false)
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

    /*
    @Column(nullable = true)
    @Embedded
    //TODO abklären martin johannes
    private DSADate weatherCollectedDate;
    */

    @Size(max = 1000)
    @Column(nullable = true, length = 1000)
    private String comment;

    @OneToMany(mappedBy = "pk.location1", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<LocationConnection> connections1 = new HashSet<>();

    @OneToMany(mappedBy = "pk.location2", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<LocationConnection> connections2 = new HashSet<>();

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) // no getter+setter to avoid lazy loading exceptions :)
    private Set<Trader> traders;

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

    /*
    public DSADate getWeatherCollectedDate() {
        return weatherCollectedDate;
    }

    public void setWeatherCollectedDate(DSADate weatherCollectedDate) {
        this.weatherCollectedDate = weatherCollectedDate;
    }
    */

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

    public Set<LocationConnection> getAllConnections() {
        Set<LocationConnection> result = new HashSet<>(connections1.size() + connections2.size());
        result.addAll(connections1);
        result.addAll(connections2);
        return result;
    }

    public void addConnection(LocationConnection locationConnection) {
        if (this.equals(locationConnection.getLocation1())) {
            connections1.add(locationConnection);
        } else if (this.equals(locationConnection.getLocation2())) {
            connections2.add(locationConnection);
        }
    }

    public void removeConnection(LocationConnection locationConnection) {
        connections1.remove(locationConnection);
        connections2.remove(locationConnection);
    }

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

        if (id != null ? !id.equals(location.id) : location.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

	@Override
	public String toString() {
		return name;
	}

	/*
    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", region=" + region +
                '}';
    }
    */


	@OneToMany(mappedBy = "location", cascade = CascadeType.ALL)
	private Collection<Tavern> taverns;

	public Collection<Tavern> getTaverns() {
		return taverns;
	}

	public void setTaverns(Collection<Tavern> taverns) {
		this.taverns = taverns;
	}
}
