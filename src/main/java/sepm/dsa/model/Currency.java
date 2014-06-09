package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Entity
@Table(name = "currencies")
public class Currency implements BaseModel {

    private static final long serialVersionUID = 5329256729754963420L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String name;

    @NotNull
    @Column(nullable = false, precision = 9, scale = 3)
    private BigDecimal valueToBaseRate;  // relative value to base rate TODO change to Integer

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

    public BigDecimal getValueToBaseRate() {
        return valueToBaseRate;
    }

    public void setValueToBaseRate(BigDecimal valueToBaseRate) {
        this.valueToBaseRate = valueToBaseRate;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;

        Currency currency = (Currency) o;

        if (id != null ? !id.equals(currency.id) : currency.id != null) return false;
        if (name != null ? !name.equals(currency.name) : currency.name != null) return false;
        if (valueToBaseRate != null ? !valueToBaseRate.equals(currency.valueToBaseRate) : currency.valueToBaseRate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (valueToBaseRate != null ? valueToBaseRate.hashCode() : 0);
        return result;
    }
}
