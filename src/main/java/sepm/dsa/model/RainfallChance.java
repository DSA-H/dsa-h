package sepm.dsa.model;

public enum RainfallChance {

    VERY_LOW(0, "sehr niedrig"),
    LOW(1, "niedrig"),
    MEDIUM(2, "mittel"),
    HIGH(3, "hoch"),
    VERY_HIGH(4, "sehr hoch");

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
        RainfallChance right = null;        // Default
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
        return name;
    }

    public String getName() {
        return name;
    }

}
