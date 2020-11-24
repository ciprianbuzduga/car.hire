package ro.agilehub.javacourse.car.hire.user.document;

public enum UserStatusEnum {

	ACTIVE("ACTIVE"),

	DELETED("DELETED");

	private String value;

	UserStatusEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public static UserStatusEnum fromValue(String value) {
		for (UserStatusEnum b : UserStatusEnum.values()) {
			if (b.value.equals(value)) {
				return b;
			}
		}
		throw new IllegalArgumentException("Unexpected value '" + value + "'");
	}
}
