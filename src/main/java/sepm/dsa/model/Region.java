package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;
import sepm.dsa.service.path.PathNode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "regions")
public class Region implements BaseModel, PathNode {
    private static final long serialVersionUID = 5890354733231481712L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60)
    private String name;

    @Size(max = 1000)
    @Column(nullable = true, length = 1000)
    private String comment;

    @NotBlank
    @Size(max = 6)
    @Column(nullable = false, length = 6)
    private String color;

    @NotNull
    @Column(nullable = false)
    private Integer temperatureId;

    @NotNull
    @Column(nullable = false)
    private Integer rainfallChanceId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.region1", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RegionBorder> borders1 = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "pk.region2", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RegionBorder> borders2 = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region", cascade = CascadeType.REMOVE)
    private Set<Location> locations = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "product_regions", joinColumns = { @JoinColumn(name = "regionId") }, inverseJoinColumns = { @JoinColumn(name = "productId") })
    private Set<Product> products = new HashSet<>();

//    public Set<Location> getLocations() {
//        return locations;
//    }
//
//    public void setLocations(Set<Location> locations) {
//        this.locations = locations;
//    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        } else {
            this.temperatureId = temperature.getValue();
        }
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
        } else {
            this.rainfallChanceId = rainfallChance.getValue();
        }    
    }

    public Set<RegionBorder> getBorders1() {
        return borders1;
    }

    public void setBorders1(Set<RegionBorder> borders1) {
        this.borders1 = borders1;
    }

    public Set<RegionBorder> getBorders2() {
        return borders2;
    }

    public void setBorders2(Set<RegionBorder> borders2) {
        this.borders2 = borders2;
    }

    public Set<RegionBorder> getAllBorders() {
        Set<RegionBorder> result = new HashSet<>(borders1.size() + borders2.size());
        result.addAll(borders1);
        result.addAll(borders2);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Region region = (Region) o;

        if (id != null ? !id.equals(region.id) : region.id != null) return false;

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
}
