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

    @OneToOne
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
}
