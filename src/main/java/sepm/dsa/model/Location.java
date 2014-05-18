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