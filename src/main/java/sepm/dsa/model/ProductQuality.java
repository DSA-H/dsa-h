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

    public float getQualityProbabilityValue() {
        switch (value) {
            case -2: return 0.05f;
            case -1: return 0.15f;
            case 0: return 0.7f;
            case 1: return 0.9f;
            case 2: return 0.98f;
            case 3: return 1f;
            default: return 0;
        }
    }

    public float getQualityPriceFactor() {
        switch (value) {
            case -2: return 0.5f;
            case -1: return 0.8f;
            case 0: return 1f;
            case 1: return 2f;
            case 2: return 5f;
            case 3: return 10f;
            default: return 0;
        }
    }

    @Override
    public String toString() {
        return name() + "(" + this.getValue() + ")";
    }

    public String getName() {
        return name;
    }
}
