package sepm.dsa.model;


public enum Weather {

    SUNNY(0, "sonnig"),
    RAINY(1, "regnerisch"),
    CLOUDY(2, "bewölkt");

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
        Weather right = null;        // Default
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
