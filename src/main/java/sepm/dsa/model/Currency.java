package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "currencies")
public class Currency implements BaseModel {

    private static final long serialVersionUID = 1524655153502866478L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String name;

    @NotBlank
    @Size(max = 5)
    @Column(nullable = false, length = 5)
    private String shortName;

    @NotNull
    @Column(nullable = false)
    private Integer valueToBaseRate;

    @ManyToMany(mappedBy = "currencies", fetch = FetchType.LAZY)
    private Set<CurrencySet> currencySets = new HashSet<>(5);


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
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        if (id != null ? !id.equals(currency.id) : currency.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Set<CurrencySet> getCurrencySets() {
        return currencySets;
    }

    public void setCurrencySets(Set<CurrencySet> currencySets) {
        this.currencySets = currencySets;
    }
}
