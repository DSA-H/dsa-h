package sepm.dsa.model;

public enum TownSize {

    MINI(0, "Winzig"),
    LITTLE(1, "Klein"),
    MEDIUM(2, "Mittel"),
    BIG(3, "Groß"),
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
        TownSize right = null;
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
        return name;
    }

    public String getName() {
        return name;
    }

}
