package ro.agilehub.javacourse.car.hire.user.document;

public enum CountryStatusEnum {

	ACTIVE("ACTIVE"),

	INACTIVE("INACTIVE");

	private String value;

	CountryStatusEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public static CountryStatusEnum fromValue(String value) {
		for (CountryStatusEnum b : CountryStatusEnum.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}
