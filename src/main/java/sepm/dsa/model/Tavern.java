package sepm.dsa.model;

import java.io.Serializable;

public class Tavern implements Serializable {

    private static final long serialVersionUID = -2259554288598225744L;

    private Integer id;
    private String name;
    private Integer xPos;
    private Integer yPos;
    private Integer usage;
    //    private ??? overnightStayPrice;
    private DSADate onDate;
    private Location location;

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
