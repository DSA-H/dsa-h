package sepm.dsa.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
public class Product implements Serializable {
    private static final long serialVersionUID = 5890354733231481712L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer cost;

    /*
    @NotNull
    @Column(nullable = false)
    private Integer unitId;*/

    @NotNull
    @Column(nullable = false)
    private Integer attributeId;

    @Size(max = 1000)
    @Column(nullable = true, length = 1000)
    private String comment;

    @NotNull
    @Column(nullable = false)
    private Boolean quality;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "product_categories", joinColumns = { @JoinColumn(name = "productId") }, inverseJoinColumns = { @JoinColumn(name = "categoryId") })
    private Set<ProductCategory> categories;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "product_regions", joinColumns = { @JoinColumn(name = "productId") }, inverseJoinColumns = { @JoinColumn(name = "regionId") })
    private Set<Region> productionRegions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer productID) {
        this.id = productID;
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

    /*public Integer getUnit() {
        if (attributeId == null){
            return null;
        }else {
            return unitId;
        }
    }
    public void setUnit(ProductUnit unit) {
        if (unit==null){
            this.unitId = null;
        }else{
            this.unitId = unit.getId();
        }
    }*/

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (attributeId != null ? !attributeId.equals(product.attributeId) : product.attributeId != null) return false;
        if (comment != null ? !comment.equals(product.comment) : product.comment != null) return false;
        if (cost != null ? !cost.equals(product.cost) : product.cost != null) return false;
        if (name != null ? !name.equals(product.name) : product.name != null) return false;
        if (quality != null ? !quality.equals(product.quality) : product.quality != null) return false;
        //if (unitId != null ? !unitId.equals(product.unitId) : product.unitId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        //result = 31 * result + (unitId != null ? unitId.hashCode() : 0);
        result = 31 * result + (attributeId != null ? attributeId.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (quality != null ? quality.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}