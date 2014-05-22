package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Table(name = "currencies")
public class Currency implements Serializable {

    private static final long serialVersionUID = 5329256729754963420L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String name;

    @NotNull
    @Column(nullable = false)
    //TODO check this @Johannes
    private Integer valueToBaseRate;  // relative value to base rate

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public Integer getValueToBaseRate() {
        return valueToBaseRate;
    }

    public void setValueToBaseRate(Integer valueToBaseRate) {
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

        if (!id.equals(currency.id)) return false;
        if (!name.equals(currency.name)) return false;
        if (!valueToBaseRate.equals(currency.valueToBaseRate)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + valueToBaseRate.hashCode();
        return result;
    }
}
