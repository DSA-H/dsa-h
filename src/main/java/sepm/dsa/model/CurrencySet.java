package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "currencySets")
public class CurrencySet implements BaseModel {

    private static final long serialVersionUID = -6854851722589807382L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "currencySet_currencies",
            joinColumns = { @JoinColumn(name = "currencySet_id") },
            inverseJoinColumns = { @JoinColumn(name = "currency_id") })
    private Set<Currency> currencies = new HashSet<>(5);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CurrencySet that = (CurrencySet) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }


//    public Set<Currency> getCurrencies() {
//        return currencies;
//    }
//
//    public void setCurrencies(Set<Currency> currencies) {
//        this.currencies = currencies;
//    }

    public void addCurrency(Currency currency) {
        this.currencies.add(currency);
    }

    public void removeCurrency(Currency currency) {
        this.currencies.remove(currency);
    }

    public int currenciesSize() {
        return this.currencies.size();
    }

    public void clearCurrencies() {
        this.currencies.clear();
    }

}
