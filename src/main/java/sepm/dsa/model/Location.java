package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

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

    //    @NotBlank
//    @Size(max = 100, min = 1)
//    @Column(nullable = false, length = 100)
    //TODO wie ist es mit cascade delete?
    @OneToOne(fetch = FetchType.EAGER, mappedBy = "region")
    private Region region;

    @NotNull
    @Column(nullable = false)
    private Integer xCoord;

    @NotNull
    @Column(nullable = false)
    private Integer yCoord;

    @NotNull
    @Column(nullable = false)
    private TownSize size;

    //TODO check if optional
    private String planFilepath;

    @NotNull
    @Column(nullable = false)
    private Integer height;

    @NotNull
    @Column(nullable = false)
    private Weather weather;

    @NotNull
    @Column(nullable = false)
    private DSADate weatherCollectedDate;

    @Size(max = 1000)
    @Column(nullable = true, length = 1000)
    private String comment;

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
        return size;
    }

    public void setSize(TownSize size) {
        this.size = size;
    }

    public String getPlanFilepath() {
        return planFilepath;
    }

    public void setPlanFilepath(String planFilepath) {
        this.planFilepath = planFilepath;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (comment != null ? !comment.equals(location.comment) : location.comment != null) return false;
        if (height != null ? !height.equals(location.height) : location.height != null) return false;
        if (id != null ? !id.equals(location.id) : location.id != null) return false;
        if (name != null ? !name.equals(location.name) : location.name != null) return false;
        if (planFilepath != null ? !planFilepath.equals(location.planFilepath) : location.planFilepath != null)
            return false;
        if (region != null ? !region.equals(location.region) : location.region != null) return false;
        if (size != location.size) return false;
        if (weather != location.weather) return false;
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
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (planFilepath != null ? planFilepath.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        result = 31 * result + (weather != null ? weather.hashCode() : 0);
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
