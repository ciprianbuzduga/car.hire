package ro.agilehub.javacourse.car.hire.user.execption;

public class UserAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1751649026547493884L;
	private static final String A_USER_ALREADY_EXISTS_WITH_EMAIL_S
			= "A user already exists with email %s";

	public UserAlreadyExistsException(String email) {
		super(String.format(A_USER_ALREADY_EXISTS_WITH_EMAIL_S, email));
	}
}
