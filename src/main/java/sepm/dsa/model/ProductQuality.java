package sepm.dsa.model;

public enum ProductQuality {

	KATASTROPHAL(0, "katastrophal"),
	SCHLECHT(1, "schlecht"),
	NORMAL(2, "normal"),
	BESSER(3, "besser"),
	AUSSERGEWOEHNLICH(4, "ausergewöhnlich"),
	UEBERRAGEND(5, "überragend");

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
				return 0.15f;
			case 2:
				return 0.7f;
			case 3:
				return 0.9f;
			case 4:
				return 0.98f;
			case 5:
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
				return 0.8f;
			case 2:
				return 1f;
			case 3:
				return 2f;
			case 4:
				return 5f;
			case 5:
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
