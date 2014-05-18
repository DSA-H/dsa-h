package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "assortmentNatures")
public class AssortmentNature {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @OneToMany
    @JoinColumn(nullable = true)
    private ProductCategory productCategory;

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
