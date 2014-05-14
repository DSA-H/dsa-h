package sepm.dsa.model;

/**
 * Created by Michael on 13.05.2014.
 */
public enum RainfallChance {

    DESSERT(1, "Wüste"),
    LOW(2, "niedrig"),
    MEDIUM(3, "mittel"),
    HIGH(4, "hoch"),
    MONSUN(5, "Monsun");

    private int value;
    private String name;

    RainfallChance(int value, String name) {
        this.value = value;
        this.name = name;
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

    public String getName() {
        return name;
    }

}
