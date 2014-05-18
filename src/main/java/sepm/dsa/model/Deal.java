package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "deals")
public class Deal {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private boolean purchase;

    @NotNull
    @Column(nullable = false)
    private Integer price;

    @NotNull
    @Column(nullable = false)
    private Integer amount;

    @NotNull
    @Column(nullable = false)
    private Integer qualityId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private DSADate date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isPurchase() {
        return purchase;
    }

    public void setPurchase(boolean purchase) {
        this.purchase = purchase;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public ProductQuality getQuality() {
        if (qualityId == null) {
            return null;
        }
        return ProductQuality.parse(qualityId);
    }

    public void setquality(ProductQuality quality) {
        if (quality == null) {
            this.qualityId = null;
        }
        this.qualityId = quality.getValue();
    }

    public DSADate getDate() {
        return date;
    }

    public void setDate(DSADate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Deal deal = (Deal) o;

        if (purchase != deal.purchase) return false;
        if (amount != null ? !amount.equals(deal.amount) : deal.amount != null) return false;
        if (date != null ? !date.equals(deal.date) : deal.date != null) return false;
        if (id != null ? !id.equals(deal.id) : deal.id != null) return false;
        if (price != null ? !price.equals(deal.price) : deal.price != null) return false;
        if (qualityId != null ? !qualityId.equals(deal.qualityId) : deal.qualityId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (purchase ? 1 : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (qualityId != null ? qualityId.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
