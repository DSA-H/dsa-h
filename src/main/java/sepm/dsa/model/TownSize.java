package sepm.dsa.model;

/**
 * Created by Michael on 16.05.2014.
 */
public enum TownSize {

    MINI(0, "Mini"),
    LITTLE(1, "klein"),
    MEDIUM(2, "mittel"),
    BIG(3, "hoch"),
    METROPOL(4, "Metropole");

    private int value;
    private String name;

    TownSize(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static TownSize parse(int value) {
        TownSize right = null;        // Default
        for (TownSize item : TownSize.values()) {
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