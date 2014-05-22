package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "assortmentNatures")
public class AssortmentNature implements Serializable {
    private static final long serialVersionUID = 2957293850231481715L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    private ProductCategory productCategory;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private TraderCategory traderCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssortmentNature that = (AssortmentNature) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    @Column(nullable = false)
    private Integer defaultOccurence;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public Integer getDefaultOccurence() {
        return defaultOccurence;
    }

    public void setDefaultOccurence(Integer defaultOccurence) {
        this.defaultOccurence = defaultOccurence;
    }

    public TraderCategory getTraderCategory() {
        return traderCategory;
    }

    public void setTraderCategory(TraderCategory traderCategory) {
        this.traderCategory = traderCategory;
    }
}
