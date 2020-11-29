package ro.agilehub.javacourse.car.hire.fleet.document;

public enum CarStatusEnum {

	ACTIVE("ACTIVE"),

	DELETED("DELETED");

	private String value;

	CarStatusEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public static CarStatusEnum fromValue(String value) {
		for (CarStatusEnum b : CarStatusEnum.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}
