package sepm.dsa.model;

public enum DistancePreferrence {
    REGION(0, "regional"),
    GLOBAL(1, "global");

    private int value;
    private String name;

    DistancePreferrence(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static DistancePreferrence parse(int value) {
        DistancePreferrence right = null;        // Default
        for (DistancePreferrence item : DistancePreferrence.values()) {
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
