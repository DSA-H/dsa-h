package sepm.dsa.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Michael on 11.05.2014.
 */

@Entity
@Table(name = "borders")
@AssociationOverrides({
        @AssociationOverride(name = "pk.region1", joinColumns = @JoinColumn(name = "region1")),
        @AssociationOverride(name = "pk.region2", joinColumns = @JoinColumn(name = "region2"))
})
public class RegionBorder implements Serializable {

    @EmbeddedId
    private RegionBorderPk pk;        // invariant: not null

    @Column(nullable = false)
    private Integer borderCost;

    public RegionBorder() {
        this.pk = new RegionBorderPk();
    }

    public Integer getBorderCost() {
        return borderCost;
    }

    public void setBorderCost(Integer borderCost) {
        this.borderCost = borderCost;
    }

    public RegionBorderPk getPk() {
        return pk;
    }

    public void setPk(RegionBorderPk pk) {
        this.pk = pk;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegionBorder border = (RegionBorder) o;

        if (borderCost != null ? !borderCost.equals(border.borderCost) : border.borderCost != null) return false;
        if (pk != null ? !pk.equals(border.pk) : border.pk != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pk != null ? pk.hashCode() : 0;
        result = 31 * result + (borderCost != null ? borderCost.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RegionBorder{" +
                "pk=" + pk +
                ", borderCost=" + borderCost +
                '}';
    }
}
