package ro.agilehub.javacourse.car.hire.rental.document;

public enum RentalStatusEnum {

	ACTIVE("ACTIVE"),

	CANCELLED("CANCELLED");

	private String value;

	RentalStatusEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public static RentalStatusEnum fromValue(String value) {
		for (RentalStatusEnum b : RentalStatusEnum.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}
