package sepm.dsa.model;

public enum ProductAttribute {

    NORMAL(0, "normal"),
    VERDERBLICH(1, "verderblich"),
    LAGERBAR(2, "lagerbar"),
    ZERBRECHLICH(3, "zerbrechlich"),
    GEFAEHRLICH(4, "gefährlich");

    private int value;
    private String name;

    ProductAttribute(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static ProductAttribute parse(int value) {
        ProductAttribute right = null;        // Default
        for (ProductAttribute item : ProductAttribute.values()) {
            if (item.getValue() == value) {
                right = item;
                break;
            }
        }
        return right;
    }

    /**
     * @return factor from DSA-rules for price calculation
     */
    public float getProductTransporabilityFactor() {
        switch (value) {
            case 0: return 1f;
            case 1: return 1.5f;
            case 2: return 0.875f;
            case 3: return 1.5f;
            case 4: return 1.5f;
            default: return 0f;
        }
    }

    /**
     * @return factor from DSA-rules for availability calculation
     */
    public int getProductTranporabilitySubtrahend() {
        switch (value) {
            case 0: return 0;
            case 1: return 320;
            case 2: return 200;
            case 3: return 0;
            case 4: return 0; //todo: Wert von Laurids
            default: return 0;
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }
}
