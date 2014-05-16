package sepm.dsa.model;


public enum Weather {

    MINI(0, "Mini"),
    LITTLE(1, "klein"),
    MEDIUM(2, "mittel"),
    BIG(3, "hoch"),
    METROPOL(4, "Metropole");

    private int value;
    private String name;

    Weather(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static Weather parse(int value) {
        Weather right = null;		// Default
        for (Weather item : Weather.values()) {
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
