package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "productCategories")
public class ProductCategory implements BaseModel {

    private static final long serialVersionUID = 3899365080989423282L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60)
    private String name;

    @ManyToOne
    @JoinColumn
    private ProductCategory parent;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<ProductCategory> childs = new HashSet<>();

    @ManyToMany(mappedBy = "categories", fetch = FetchType.EAGER)
//    @JoinTable(name = "product_categories",
//            joinColumns = { @JoinColumn(name = "categoryId") },     // is joinColumns/inverse correct here?
//            inverseJoinColumns = { @JoinColumn(name = "productId") })
    private Set<Product> products = new HashSet<>();

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "pk.productCategory", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    @MapKey(name="pk.traderCategory")
//    private Map<TraderCategory, AssortmentNature> assortments = new HashMap<>();u
//
//    public Map<TraderCategory, AssortmentNature> getAssortments() {
//        return assortments;
//    }
//
//    public void setAssortments(Map<TraderCategory, AssortmentNature> assortments) {
//        this.assortments = assortments;
//    }

    //    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @JoinColumn(name = "productcategory_id", nullable = false)
//    private Set<AssortmentNature> assortmentNatures = new HashSet<>();

//    public Set<AssortmentNature> getAssortmentNatures() {
//        return assortmentNatures;
//    }
//
//    public void setAssortments(Map<TraderCategory, AssortmentNature> assortments) {
//        this.assortments = assortments;
//    }

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

    public ProductCategory getParent() {
        return parent;
    }

    public void setParent(ProductCategory parent) {
        this.parent = parent;
    }

    public Set<ProductCategory> getChilds() {
        return childs;
    }

    public void setChilds(Set<ProductCategory> childs) {
        this.childs = childs;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductCategory that = (ProductCategory) o;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        return result;
    }
    @Override
    public String toString(){
        return name;
    }
}
