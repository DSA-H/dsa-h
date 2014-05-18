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

    @Size(max = 1000)
    @Column(nullable = true, length = 1000)
    private String comment;

    @NotNull
    @Column(nullable = false)
    private Integer mut;

    @NotNull
    @Column(nullable = false)
    private Integer intelligence;

    @NotNull
    @Column(nullable = false)
    private Integer charisma;

    @NotNull
    @Column(nullable = false)
    private Integer convince;

    @ManyToOne
    @JoinColumn(nullable = true)
    private TraderCategory category;

    @NotNull
    @Column(nullable = false)
    private Location location;

    @OneToMany
    @JoinColumn(nullable = false)
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

    public Integer getMut() {
        return mut;
    }

    public void setMut(Integer mut) {
        this.mut = mut;
    }

    public Integer getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(Integer intelligence) {
        this.intelligence = intelligence;
    }

    public Integer getCharisma() {
        return charisma;
    }

    public void setCharisma(Integer charisma) {
        this.charisma = charisma;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trader trader = (Trader) o;

        if (category != null ? !category.equals(trader.category) : trader.category != null) return false;
        if (charisma != null ? !charisma.equals(trader.charisma) : trader.charisma != null) return false;
        if (comment != null ? !comment.equals(trader.comment) : trader.comment != null) return false;
        if (convince != null ? !convince.equals(trader.convince) : trader.convince != null) return false;
        if (deals != null ? !deals.equals(trader.deals) : trader.deals != null) return false;
        if (id != null ? !id.equals(trader.id) : trader.id != null) return false;
        if (intelligence != null ? !intelligence.equals(trader.intelligence) : trader.intelligence != null) return false;
        if (location != null ? !location.equals(trader.location) : trader.location != null) return false;
        if (mut != null ? !mut.equals(trader.mut) : trader.mut != null) return false;
        if (name != null ? !name.equals(trader.name) : trader.name != null) return false;
        if (offers != null ? !offers.equals(trader.offers) : trader.offers != null) return false;
        if (size != null ? !size.equals(trader.size) : trader.size != null) return false;
        if (xPos != null ? !xPos.equals(trader.xPos) : trader.xPos != null) return false;
        if (yPos != null ? !yPos.equals(trader.yPos) : trader.yPos != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (xPos != null ? xPos.hashCode() : 0);
        result = 31 * result + (yPos != null ? yPos.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (mut != null ? mut.hashCode() : 0);
        result = 31 * result + (intelligence != null ? intelligence.hashCode() : 0);
        result = 31 * result + (charisma != null ? charisma.hashCode() : 0);
        result = 31 * result + (convince != null ? convince.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (offers != null ? offers.hashCode() : 0);
        result = 31 * result + (deals != null ? deals.hashCode() : 0);
        return result;
    }
}
