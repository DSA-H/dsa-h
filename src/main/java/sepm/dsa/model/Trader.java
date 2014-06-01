package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "traders")
public class Trader implements BaseModel {

    private static final long serialVersionUID = 2857293850231481712L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer size;

    @Column(nullable = true)
    private Integer xPos;

    @Column(nullable = true)
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
    @JoinColumn(nullable = false)
    private TraderCategory category;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Location location;

//    @OneToMany(cascade = CascadeType.ALL)// LAZY, otherwise offer will not be delted cascading!
//    @JoinColumn(nullable = false)
    @OneToMany(mappedBy = "trader", cascade = CascadeType.REMOVE)
    private Set<Offer> offers = new HashSet<>();

    @OneToMany(mappedBy = "trader")
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

        if (id != null ? !id.equals(trader.id) : trader.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

	@Override
	public String toString() { return category.getName() + " " + name; }
}
