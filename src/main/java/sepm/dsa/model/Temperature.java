package sepm.dsa.model;

public enum Temperature {

    VERY_LOW(0, "kalt"),
    LOW(1, "kühl"),
    MEDIUM(2, "mittel"),
    HIGH(3, "warm"),
    VERY_HIGH(4, "heiß");

    private int value;
    private String name;

    Temperature(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static Temperature parse(int value) {
        Temperature right = null;        // Default
        for (Temperature item : Temperature.values()) {
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
