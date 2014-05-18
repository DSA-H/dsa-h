package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "assortmentNatures")
public class AssortmentNature implements Serializable{
    private static final long serialVersionUID = 2957293850231481715L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = true)
    private ProductCategory productCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssortmentNature that = (AssortmentNature) o;

        if (defaultOccurence != null ? !defaultOccurence.equals(that.defaultOccurence) : that.defaultOccurence != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (productCategory != null ? !productCategory.equals(that.productCategory) : that.productCategory != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productCategory != null ? productCategory.hashCode() : 0);
        result = 31 * result + (defaultOccurence != null ? defaultOccurence.hashCode() : 0);
        return result;
    }

    @NotNull
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
}
