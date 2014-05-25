package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "assortmentNatures")
public class AssortmentNature implements BaseModel {
    private static final long serialVersionUID = 2957293850231481715L;

    @EmbeddedId
    private Pk pk = new Pk();

    @NotNull
    @Min(value = 1)
    @Max(value = 100)
    @Column(nullable = false)
    private Integer defaultOccurence;

    public ProductCategory getProductCategory() {
        return pk.productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.pk.productCategory = productCategory;
    }

    public Integer getDefaultOccurence() {
        return defaultOccurence;
    }

    public void setDefaultOccurence(Integer defaultOccurence) {
        this.defaultOccurence = defaultOccurence;
    }

    public TraderCategory getTraderCategory() {
        return pk.traderCategory;
    }

    public void setTraderCategory(TraderCategory traderCategory) {
        this.pk.traderCategory = traderCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AssortmentNature that = (AssortmentNature) o;

        if (pk != null ? !pk.equals(that.pk) : that.pk != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pk != null ? pk.hashCode() : 0;
    }

    @Embeddable
    protected static class Pk implements Serializable {

        @ManyToOne
        @JoinColumn(name = "productcategory_id", nullable = false, insertable=false, updatable=false)
        private ProductCategory productCategory;

        @ManyToOne
        @JoinColumn(name = "tradercategory_id", nullable = false, insertable = false, updatable = false)
        private TraderCategory traderCategory;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pk pk = (Pk) o;

            if (productCategory != null ? !productCategory.equals(pk.productCategory) : pk.productCategory != null)
                return false;
            if (traderCategory != null ? !traderCategory.equals(pk.traderCategory) : pk.traderCategory != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = productCategory != null ? productCategory.hashCode() : 0;
            result = 31 * result + (traderCategory != null ? traderCategory.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Pk{" +
                    "productCategory=" + productCategory +
                    ", traderCategory=" + traderCategory +
                    '}';
        }
    }

}
