package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "offers")
public class Offer {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Product product;

    @NotNull
    @Column(nullable = false)
    private Integer amount;

    @NotNull
    @Column(nullable = false)
    private Integer pricePerUnit;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ProductQuality quality;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Integer pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public ProductQuality getQuality() {
        return quality;
    }

    public void setQuality(ProductQuality quality) {
        this.quality = quality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Offer offer = (Offer) o;

        if (amount != null ? !amount.equals(offer.amount) : offer.amount != null) return false;
        if (id != null ? !id.equals(offer.id) : offer.id != null) return false;
        if (pricePerUnit != null ? !pricePerUnit.equals(offer.pricePerUnit) : offer.pricePerUnit != null) return false;
        if (product != null ? !product.equals(offer.product) : offer.product != null) return false;
        if (quality != offer.quality) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (pricePerUnit != null ? pricePerUnit.hashCode() : 0);
        result = 31 * result + (quality != null ? quality.hashCode() : 0);
        return result;
    }
}
