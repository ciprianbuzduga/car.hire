package ro.agilehub.javacourse.car.hire.user.execption;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import java.util.NoSuchElementException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ro.agilehub.javacourse.car.hire.api.exception.PatchException;
import ro.agilehub.javacourse.car.hire.api.model.ErrorDTO;
import ro.agilehub.javacourse.car.hire.api.model.ValidationDTO;

@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
public class UserRestExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String CODE_BAD_REQUEST = "BAD_REQUEST";
	private static final String DUPLICATE_EMAIL = "DUPLICATE_EMAIL";
	private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
	private static final String RESOURCE_NOT_FOUND_ERROR = "RESOURCE_NOT_FOUND_ERROR";

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(
			MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = ex.getParameterName() + " parameter is missing";
		return createBadRequestEntity(CODE_BAD_REQUEST, error, ex.getParameterName());
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
			HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = ex.getMessage();
		return createBadRequestEntity(CODE_BAD_REQUEST, error, null);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = ex.getMessage();
		return createBadRequestEntity(CODE_BAD_REQUEST, error, null);
	}

	@ExceptionHandler(value = UserAlreadyExistsException.class)
	ResponseEntity<Object> handleUserAlreadyExistsException(
			UserAlreadyExistsException ex, WebRequest request) {
		String error = ex.getMessage();
		return createBadRequestEntity(DUPLICATE_EMAIL, error, "email");
	}

	@ExceptionHandler(value = ServerErrorException.class)
	ResponseEntity<ErrorDTO> handleServerErrorException(
			ServerErrorException ex, WebRequest request) {
		String error = ex.getMessage();
		ErrorDTO body = new ErrorDTO();
		body.setCode(INTERNAL_SERVER_ERROR);
		body.setMessage(error);
		return new ResponseEntity<ErrorDTO>(body, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(value = NoSuchElementException.class)
	ResponseEntity<ErrorDTO> handleNoSuchElementException(
			NoSuchElementException ex, WebRequest request) {
		String error = ex.getMessage();
		ErrorDTO body = new ErrorDTO();
		body.setCode(RESOURCE_NOT_FOUND_ERROR);
		body.setMessage(error);
		return new ResponseEntity<ErrorDTO>(body, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(value = PatchException.class)
	ResponseEntity<Object> handlePatchUserException(
			PatchException ex, WebRequest request) {
		String error = ex.getMessage();
		return createBadRequestEntity(CODE_BAD_REQUEST, error, ex.getField());
	}

	private ResponseEntity<Object> createBadRequestEntity(String code, String error,
			String field) {
		ValidationDTO dto = new ValidationDTO();
		dto.setCode(code);
		dto.setDescription(error);
		dto.setField(field);
		return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
	}
}
