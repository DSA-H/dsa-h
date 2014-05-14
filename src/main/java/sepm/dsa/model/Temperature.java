package sepm.dsa.model;

/**
 * Created by Michael on 13.05.2014.
 */
public enum Temperature {

    ARCTIC(1, "arktisch"),
    LOW(2, "niedrig"),
    MEDIUM(3, "mittel"),
    HIGH(4, "hoch"),
    VULCANO(5, "vulkano");

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
        Temperature right = null;		// Default
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
        return name() + "(" + this.getValue() + ")";
    }

    public String getName() {
        return name;
    }

}
