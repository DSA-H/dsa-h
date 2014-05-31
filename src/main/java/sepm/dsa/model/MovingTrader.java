package sepm.dsa.model;

import javax.persistence.*;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@PrimaryKeyJoinColumn(name="trader")
@Entity
@Table(name = "movingTraders")
public class MovingTrader extends Trader implements Serializable {

    private static final long serialVersionUID = 2857234550231481712L;

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
        preferredTownSize = townSize.getValue();
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

    public void setAvgStayDays(Integer avgStayDays) {
        this.avgStayDays = avgStayDays;
    }

    public Long getLastMoved() {
        return lastMoved;
    }

    public Integer getAvgStayDays() {
        return avgStayDays;
    }
}
