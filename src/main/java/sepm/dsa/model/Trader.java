package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "traders")
public class Trader {

    @Id
    @GeneratedValue
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private Integer xPos;

    @Column(nullable = false)
    private Integer yPos;

    @Size(max = 1000, min = 0)
    @Column(nullable = true, length = 1000)
    private String comment;

    @NotNull
    @Column(nullable = false)
    private Integer mu;

    @NotNull
    @Column(nullable = false)
    private Integer in;

    @NotNull
    @Column(nullable = false)
    private Integer ch;

    @NotNull
    @Column(nullable = false)
    private Integer convince;

    @NotNull
    @Column(nullable = false)
    private TraderCategory category;

    @NotNull
    @Column(nullable = false)
    private Location location;

    @OneToMany
    @JoinColumn(nullable = true)
    private Set<Offer> offers = new HashSet<>();

    @OneToMany
    @JoinColumn(nullable = true)
    private Set<Deal> deals = new HashSet<>();

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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getxPos() {
        return xPos;
    }

    public void setxPos(Integer xPos) {
        this.xPos = xPos;
    }

    public Integer getyPos() {
        return yPos;
    }

    public void setyPos(Integer yPos) {
        this.yPos = yPos;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getMu() {
        return mu;
    }

    public void setMu(Integer mu) {
        this.mu = mu;
    }

    public Integer getIn() {
        return in;
    }

    public void setIn(Integer in) {
        this.in = in;
    }

    public Integer getCh() {
        return ch;
    }

    public void setCh(Integer ch) {
        this.ch = ch;
    }

    public Integer getConvince() {
        return convince;
    }

    public void setConvince(Integer convince) {
        this.convince = convince;
    }

    public TraderCategory getCategory() {
        return category;
    }

    public void setCategory(TraderCategory category) {
        this.category = category;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<Offer> getOffers() {
        return offers;
    }

    public void setOffers(Set<Offer> offers) {
        this.offers = offers;
    }

    public Set<Deal> getDeals() {
        return deals;
    }

    public void setDeals(Set<Deal> deals) {
        this.deals = deals;
    }
}
