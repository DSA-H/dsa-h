package sepm.dsa.model;

public class UnitAmount {

    Unit unit;
    Double amount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnitAmount)) return false;

        UnitAmount that = (UnitAmount) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (unit != null ? !unit.equals(that.unit) : that.unit != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = unit != null ? unit.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductUnitAmount{" +
                "productUnit=" + unit +
                ", amount=" + amount +
                '}';
    }

    public UnitAmount() {
    }

    public UnitAmount(Unit unit, Double amount) {
        this.unit = unit;
        this.amount = amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
