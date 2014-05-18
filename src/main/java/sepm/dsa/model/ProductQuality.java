package sepm.dsa.model;

public enum ProductQuality {

    KATASTROPHAL(-2, "katastrophal"),
    SCHLECHT(-1, "schlecht"),
    NORMAL(0, "normal"),
    BESSER(1, "besser"),
    AUSSERGEWOEHNLICH(2, "ausergewöhnlich"),
    UEBERRAGEND(3, "überragend");

    private int value;
    private String name;

    ProductQuality(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static ProductQuality parse(int value) {
        ProductQuality right = null;        // Default
        for (ProductQuality item : ProductQuality.values()) {
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
