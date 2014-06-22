package sepm.dsa.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@PrimaryKeyJoinColumn(name="trader")
@Entity
@Table(name = "movingTraders")
public class MovingTrader extends Trader implements Serializable {

    private static final long serialVersionUID = 8961602390107311003L;

    @Column
    private Long lastMoved;

    @NotNull
    @Column
    private Integer avgStayDays;

    @Column
    private Integer preferredTownSize;

    @Column
    @NotNull
    private Integer preferredDistance;

    public void setPreferredTownSize(TownSize townSize) {
        if(townSize != null) {
            preferredTownSize = townSize.getValue();
        }else {
            preferredTownSize = null;
        }
    }

    public TownSize getPreferredTownSize(){
        if(preferredTownSize == null) {
            return null;
        }else {
            return TownSize.parse(preferredTownSize);
        }
    }

    public void setPreferredDistance(DistancePreferrence distancePreferrence) {
        preferredDistance = distancePreferrence.getValue();
    }

    public DistancePreferrence getPreferredDistance(){
        return DistancePreferrence.parse(preferredDistance);
    }

    public void setLastMoved(Long lastMoved) {
        this.lastMoved = lastMoved;
    }

	public void setLastMoved(DSADate lastMoved) {
		this.lastMoved = lastMoved.getTimestamp();
	}

    public void setAvgStayDays(Integer avgStayDays) {
        this.avgStayDays = avgStayDays;
    }

    public Long getLastMoved() {
        return lastMoved;
    }

    public Integer getAvgStayDays() {
        return avgStayDays;
    }

    @Override
    public String toString() {
        return "fahrender Händler " + getName() + " (" + getCategory() + ")";
    }
}
