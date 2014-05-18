package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "products")
public class Product implements Serializable {
    private static final long serialVersionUID = 5890354733231481712L;

    @Id
    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60, unique = true)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer cost;

    @NotNull
    @Column(nullable = false)
    private String unit;

    @NotNull
    @Column(nullable = false)
    private Integer attributeId;

    @Size(max = 1000)
    @Column(nullable = true, length = 1000)
    private String comment;

    @NotNull
    @Column(nullable = false)
    private Boolean quality;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
        }
        return ProductAttribute.parse(attributeId);
    }

    public void setAttribute(ProductAttribute attribute) {
        if (attribute == null) {
            this.attributeId = null;
        }
        this.attributeId = attribute.getValue();
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
        if (unit != null ? !unit.equals(product.unit) : product.unit != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
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