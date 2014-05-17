package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "product")
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
    private Integer weight;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (name != null ? !name.equals(product.name) : product.name != null)
            return false;
        if (weight != null ? !weight.equals(product.weight) : product.weight != null)
            return false;
        if (cost != null ? !cost.equals(product.cost) : product.cost != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);

        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
