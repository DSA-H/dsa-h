package sepm.dsa.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "regions")
public class Region implements Serializable {
    private static final long serialVersionUID = 5890354733231481712L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, length = 6)
    private String color;

    @Column(nullable = false)
    private Integer temperatureId;

    @Column(nullable = false)
    private Integer rainfallChanceId;


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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Temperature getTemperature() {
        if (temperatureId == null) {
            return null;
        }
        return Temperature.parse(temperatureId);
    }

    public void setTemperature(Temperature temperature) {
        if (temperature == null) {
            this.temperatureId = null;
        }
        this.temperatureId = temperature.getValue();
    }


    public RainfallChance getRainfallChance() {
        if (rainfallChanceId == null) {
            return null;
        }
        return RainfallChance.parse(rainfallChanceId);
    }

    public void setRainfallChance(RainfallChance rainfallChance) {
        if (rainfallChance == null) {
            this.rainfallChanceId = null;
        }
        this.rainfallChanceId = rainfallChance.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        if (color != null ? !color.equals(region.color) : region.color != null) return false;
        if (id != null ? !id.equals(region.id) : region.id != null) return false;
        if (name != null ? !name.equals(region.name) : region.name != null) return false;
        if (rainfallChanceId != null ? !rainfallChanceId.equals(region.rainfallChanceId) : region.rainfallChanceId != null)
            return false;
        if (temperatureId != null ? !temperatureId.equals(region.temperatureId) : region.temperatureId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (temperatureId != null ? temperatureId.hashCode() : 0);
        result = 31 * result + (rainfallChanceId != null ? rainfallChanceId.hashCode() : 0);
        return result;
    }
}
