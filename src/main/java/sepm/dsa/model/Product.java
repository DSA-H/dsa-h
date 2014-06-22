package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product implements BaseModel {

    private static final long serialVersionUID = -8844302216771387723L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer cost;

    @NotNull
    @ManyToOne
    @JoinColumn(nullable = false)
    private Unit unit;

    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    @Column(nullable = false)
    private Integer occurence = 100;

    @NotNull
    @Column(nullable = false)
    private Integer attributeId;

    @Size(max = 1000)
    @Column(nullable = true, length = 1000)
    private String comment;

    @NotNull
    @Column(nullable = false)
    private Boolean quality;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "product_categories",
            joinColumns = { @JoinColumn(name = "productId") },
            inverseJoinColumns = { @JoinColumn(name = "categoryId") })
    private Set<ProductCategory> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "product_regions", joinColumns = { @JoinColumn(name = "productId") }, inverseJoinColumns = { @JoinColumn(name = "regionId") })
    private Set<Region> productionRegions = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private Set<Offer> offer = new HashSet<>();

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


    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Set<Offer> getOffer() {
        return offer;
    }

    public void setOffer(Set<Offer> offer) {
       this.offer = offer;
    }

    public ProductAttribute getAttribute() {
        if (attributeId == null) {
            return null;
        }else {
            return ProductAttribute.parse(attributeId);
        }
    }

    public void setAttribute(ProductAttribute attribute) {
        if (attribute == null) {
            this.attributeId = null;
        }else {
            this.attributeId = attribute.getValue();
        }
    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<ProductCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<ProductCategory> categories) {
        this.categories = categories;
    }

    public Set<Region> getRegions() {
        return productionRegions;
    }

    public void setRegions(Set<Region> productionRegions) {
        this.productionRegions = productionRegions;
    }

    public Boolean getQuality() {
        return quality;
    }

    public void setQuality(Boolean quality) {
        this.quality = quality;
    }

    public Integer getOccurence() {
        return occurence;
    }

    public void setOccurence(Integer occurence) {
        this.occurence = occurence;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (id != null ? !id.equals(product.id) : product.id != null) return false;

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