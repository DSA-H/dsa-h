package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "taverns")
public class Tavern implements Serializable {

    private static final long serialVersionUID = -2259554288598225744L;

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
    private Integer id;

	@Size(min = 1, max = 100)
	@Column(nullable = false, length = 100)
    private String name;

	@NotNull
	@Column(nullable = false)
    private Integer xPos;

	@NotNull
	@Column(nullable = false)
    private Integer yPos;

	@NotNull
	@Column(nullable = false)
	private Integer usage;

    //TODO: free beds, comment, ...

	// @TODO ??
//    private ??? overnightStayPrice;
//    private DSADate onDate;

	@ManyToOne
	@JoinColumn(nullable = false)
    private Location location;

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

	public Integer getUsage() {
		return usage;
	}

	public void setUsage(Integer usage) {
		this.usage = usage;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tavern tavern = (Tavern) o;

        if (id != null ? !id.equals(tavern.id) : tavern.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
