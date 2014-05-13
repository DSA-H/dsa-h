package sepm.dsa.model;

/**
 * Created by Michael on 13.05.2014.
 */
public enum Temperature {

    ARCTIC(1),
    LOW(2),
    MEDIUM(3),
    HIGH(4),
    VULCANO(5);

    private int value;

    Temperature(int value) {
        this.value = value;
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

}
