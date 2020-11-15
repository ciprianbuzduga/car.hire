package ro.agilehub.javacourse.car.hire.user.execption;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import ro.agilehub.javacourse.car.hire.api.exception.RestControllerExceptionHandler;

@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
public class UserRestExceptionHandler extends RestControllerExceptionHandler {

	private static final String DUPLICATE_EMAIL = "DUPLICATE_EMAIL";

	@ExceptionHandler(value = UserAlreadyExistsException.class)
	ResponseEntity<Object> handleUserAlreadyExistsException(
			UserAlreadyExistsException ex, WebRequest request) {
		String error = ex.getMessage();
		return createBadRequestEntity(DUPLICATE_EMAIL, error, "email");
	}

}
