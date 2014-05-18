package sepm.dsa.model;

public enum ProductAttribute {

    NORMAL(0, "normal"),
    VERDERBLICH(1, "verderblich"),
    LAGERBAR(2, "lagerbar"),
    ZERBRECHLICH(3, "zerbrichlich");

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

    @Override
    public String toString() {
        return name() + "(" + this.getValue() + ")";
    }

    public String getName() {
        return name;
    }
}
