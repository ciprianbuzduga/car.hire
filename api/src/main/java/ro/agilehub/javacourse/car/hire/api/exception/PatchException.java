package ro.agilehub.javacourse.car.hire.api.exception;

public class PatchException extends RuntimeException {
	private static final long serialVersionUID = 806977600677925032L;

	private final String field;

	public PatchException(String message) {
		this(message, null);
	}

	public PatchException(String message, String field) {
		super(message);
		this.field = field;
	}

	public String getField() {
		return field;
	}

}
