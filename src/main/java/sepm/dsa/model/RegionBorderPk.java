package sepm.dsa.model;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Created by Michael on 11.05.2014.
 */
@Embeddable
public class RegionBorderPk implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "region1", nullable = false)
    private Region region1;

    @ManyToOne
    @JoinColumn(name = "region2", nullable = false)
    private Region region2;

    public RegionBorderPk() {
    }

    public RegionBorderPk(Region region1, Region region2) {
        this.region1 = region1;
        this.region2 = region2;
    }

    public Region getRegion2() {
        return region2;
    }

    public void setRegion2(Region region2) {
        this.region2 = region2;
    }

    public Region getRegion1() {
        return region1;
    }

    public void setRegion1(Region region1) {
        this.region1 = region1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegionBorderPk borderPk = (RegionBorderPk) o;

        if (region1 != null ? !region1.equals(borderPk.region1) : borderPk.region1 != null) return false;
        if (region2 != null ? !region2.equals(borderPk.region2) : borderPk.region2 != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = region1 != null ? region1.hashCode() : 0;
        result = 31 * result + (region2 != null ? region2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RegionBorderPk{" +
                "region1=" + region1 +
                ", region2=" + region2 +
                '}';
    }

}
