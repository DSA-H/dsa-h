package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "offers")
public class Offer implements BaseModel {
    private static final long serialVersionUID = 2957223850231481777L;

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private Integer amount;

//    @ManyToOne
//    @JoinColumn(name="product_id", nullable = false, insertable=false, updatable=false)   // not owning side
    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    @NotNull
    @Column(nullable = false)
    private Integer pricePerUnit;

    @NotNull
    @Column(nullable = false)
    private Integer qualityId;

    @ManyToOne
    @JoinColumn(name = "trader_id", nullable = false, insertable=false, updatable=false)
    private Trader trader;

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

    public Integer getQualityId() {
        return qualityId;
    }

    public void setQualityId(Integer qualityId) {
        this.qualityId = qualityId;
    }

    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    public ProductQuality getQuality() {
        if (qualityId == null) {
            return null;
        }
        return ProductQuality.parse(qualityId);
    }

    public void setQuality(ProductQuality quality) {
        if (quality == null) {
            this.qualityId = null;
        } else {
            this.qualityId = quality.getValue();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Offer offer = (Offer) o;

        if (id != null ? !id.equals(offer.id) : offer.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
