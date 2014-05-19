package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "productCategories")
public class ProductCategory implements Serializable {
    private static final long serialVersionUID = 2997293850231481717L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private ProductCategory parent;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    private Set<ProductCategory> childs = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "product_categories", joinColumns = { @JoinColumn(name = "categoryId") }, inverseJoinColumns = { @JoinColumn(name = "productId") })
    private Set<Product> products;


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

        if (childs != null ? !childs.equals(that.childs) : that.childs != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        //if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        //if (products != null ? !products.equals(that.products) : that.products != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        //result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (childs != null ? childs.hashCode() : 0);
        //result = 31 * result + (products != null ? products.hashCode() : 0);
        return result;
    }
}
