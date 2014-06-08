package sepm.dsa.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "units")
public class Unit implements BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false, unique = true)
    private Integer id;

    @NotBlank
    @Size(max = 60, min = 1)
    @Column(nullable = false, length = 60)
    private String name;

    @NotBlank
    @Size(max = 10, min = 1)
    @Column(nullable = false, length = 10)
    private String shortName;

    @NotNull
    @ManyToOne
    private UnitType unitType;

    @NotNull
    @Column(nullable = false)
    private Double valueToBaseUnit;  // relative value to base unit

    //TODO besprechen exchange direkt im Modell -> @Michael will das gerne

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public Double getValueToBaseUnit() {
        return valueToBaseUnit;
    }

    public void setValueToBaseUnit(Double valueToBaseUnit) {
        this.valueToBaseUnit = valueToBaseUnit;
    }

    public Double exchange(Double amount, Unit to) {
        Double result = (amount * to.getValueToBaseUnit() / this.getValueToBaseUnit());
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Unit)) return false;

        Unit unit = (Unit) o;

        if (id != null ? !id.equals(unit.id) : unit.id != null) return false;
        if (name != null ? !name.equals(unit.name) : unit.name != null) return false;
        if (shortName != null ? !shortName.equals(unit.shortName) : unit.shortName != null) return false;
        if (unitType != null ? !unitType.equals(unit.unitType) : unit.unitType != null) return false;
        if (valueToBaseUnit != null ? !valueToBaseUnit.equals(unit.valueToBaseUnit) : unit.valueToBaseUnit != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (unitType != null ? unitType.hashCode() : 0);
        result = 31 * result + (valueToBaseUnit != null ? valueToBaseUnit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}