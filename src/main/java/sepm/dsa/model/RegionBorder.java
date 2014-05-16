package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "borders")
@AssociationOverrides({
        @AssociationOverride(name = "pk.region1", joinColumns = @JoinColumn(name = "region1")),
        @AssociationOverride(name = "pk.region2", joinColumns = @JoinColumn(name = "region2"))
})
public class RegionBorder implements Serializable {

    private static final long serialVersionUID = -5121547134534726826L;

    @EmbeddedId
    private Pk pk = new Pk();        // invariant: not null

    @NotNull
    @Min(0)
    @Column(nullable = false)
    private Integer borderCost;

    public Integer getBorderCost() {
        return borderCost;
    }

    public void setBorderCost(Integer borderCost) {
        this.borderCost = borderCost;
    }

    public Region getRegion2() {
        return pk.region2;
    }

    public void setRegion2(Region region2) {
        this.pk.region2 = region2;
    }

    public Region getRegion1() {
        return pk.region1;
    }

    public void setRegion1(Region region1) {
        this.pk.region1 = region1;
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

    public boolean equalsById(RegionBorder other) {
        if (other == null) {
            return false;
        }
        return (this.pk.region1.equals(other.pk.region1)
                && this.pk.region2.equals(other.pk.region2));
    }

    @Embeddable
    private static class Pk implements Serializable {

        private static final long serialVersionUID = 5989205421915335466L;

        // TODO BeanValidation for region1 != region2

        @ManyToOne
        @JoinColumn(name = "region1", nullable = false)
        private Region region1;

        @ManyToOne
        @JoinColumn(name = "region2", nullable = false)
        private Region region2;

        public Pk() {

        }

        @Override
        public String toString() {
            return "RegionBorderPk{" +
                    "region1=" + region1 +
                    ", region2=" + region2 +
                    '}';
        }


    }
}