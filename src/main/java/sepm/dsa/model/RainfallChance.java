package sepm.dsa.model;

/**
 * Created by Michael on 13.05.2014.
 */
public enum RainfallChance {

    DESSERT(1),
    LOW(2),
    MEDIUM(3),
    HIGH(4),
    MONSUN(5);

    private int value;

    RainfallChance(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static RainfallChance parse(int value) {
        RainfallChance right = null;		// Default
        for (RainfallChance item : RainfallChance.values()) {
            if (item.getValue() == value) {
                right = item;
                break;
            }
        }
        return right;
    }

    @Override
    public String toString() {
        return name() + "(" + this.getValue() + ")";
    }

}
