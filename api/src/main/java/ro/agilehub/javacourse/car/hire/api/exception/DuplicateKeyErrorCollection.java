package ro.agilehub.javacourse.car.hire.api.exception;

public class DuplicateKeyErrorCollection extends RuntimeException {
	private static final long serialVersionUID = 1751649026547493884L;

	public DuplicateKeyErrorCollection(String message) {
		super(message);
	}
}
