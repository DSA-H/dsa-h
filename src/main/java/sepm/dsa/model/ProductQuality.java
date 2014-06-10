package sepm.dsa.model;

public enum ProductQuality {

	MIES(0, "mies"),
	MANGELHAFT(1, "mangelhaft"),
	NORMAL(2, "normal"),
	HERAUSRAGEND(3, "herausragend"),
	AUSSERGEWOEHNLICH(4, "ausergewöhnlich");

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
			case 0:
				return 0.05f;
			case 1:
				return 0.3f;
			case 2:
				return 0.95f;
			case 3:
				return 0.995f;
			case 4:
				return 1f;
			default:
				return 0;
		}
	}

	public float getQualityPriceFactor() {
		switch (value) {
			case 0:
				return 0.5f;
			case 1:
				return 0.75f;
			case 2:
				return 1f;
			case 3:
				return 3f;
			case 4:
				return 10f;
			default:
				return 0;
		}
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public String getName() {
		return name;
	}
}
