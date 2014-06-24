package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "deals")
public class Deal implements BaseModel {

    private static final long serialVersionUID = -5132370376436032659L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotNull
    @Column(nullable = false)
    private boolean purchase;       // true if the player purchases the product, false if the player sells the product

    @NotNull
    @Column(nullable = false)
    private Integer price;

    @NotNull
    @Column(nullable = false)
    private Integer discount = 0;

    @NotNull
    @Column(nullable = false)
    private Integer amount;

    @NotNull
    @Column(nullable = false)
    private Integer qualityId;

    @NotNull
    @Column
    private Long date;

    @ManyToOne
    @JoinColumn
    private Trader trader;

    @ManyToOne
    @JoinColumn
    private Product product;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Unit unit;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Player player;

    @NotBlank
    @Column(nullable = false)
    private String productName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String locationName;


    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isPurchase() {
        return purchase;
    }

    /**
     * @param purchase true if the player purchases the product, false if the player sells the product
     */
    public void setPurchase(boolean purchase) {
        this.purchase = purchase;
    }

    /**
     * @return true if the player purchases the product, false if the player sells the product
     */
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
        } else {
            this.qualityId = quality.getValue();
        }
    }

    public DSADate getDate() {
        return new DSADate(date);
    }

    public void setDate(DSADate date) {
        this.date = date.getTimestamp();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public String toString() {
        return "Deal{" +
                "id=" + id +
                ", purchase=" + purchase +
                ", price=" + price +
                ", amount=" + amount +
                ", qualityId=" + qualityId +
                ", date=" + date +
                ", trader=" + trader +
                ", product=" + product +
                ", unit=" + unit +
                ", player=" + player +
                ", productName='" + productName + '\'' +
                ", locationName='" + locationName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Deal deal = (Deal) o;

        if (id != null ? !id.equals(deal.id) : deal.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer priceWithDiscount() {
        return price.intValue() * (100 - discount) / 100;
    }

}
