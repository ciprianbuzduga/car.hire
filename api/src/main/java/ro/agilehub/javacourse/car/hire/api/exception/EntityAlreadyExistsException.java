package ro.agilehub.javacourse.car.hire.api.exception;

public class EntityAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = -1507033940246755276L;

	public EntityAlreadyExistsException(String collection, String field,
			Object value) {
		super(String.format("A record in collection %s already exists "
				+ "with field %s and value %s", collection, field, value));
	}
}
