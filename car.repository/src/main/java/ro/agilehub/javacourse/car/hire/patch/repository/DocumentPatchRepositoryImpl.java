package ro.agilehub.javacourse.car.hire.patch.repository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

public class DocumentPatchRepositoryImpl<T, ID> implements DocumentPatchRepository<T, ID> {

	private final MongoTemplate template;

	public DocumentPatchRepositoryImpl(MongoTemplate template) {
		this.template = template;
	}

	@Override
	public boolean updateDoc(Class<T> docClass, ID idValue,
			Map<String, Object> fieldValues) {
		validateIdValue(idValue);
		String collectionName = docClass.getSimpleName();
		Query query = getIdQuery(docClass, idValue);
		Update update = createUpdate(fieldValues);
		UpdateResult ur = template.updateFirst(query, update, collectionName);
		long matchedCount = ur.getMatchedCount();
		if(matchedCount == 0)
			throw new NoSuchElementException("No document found with id "
					+ idValue + " for type " + collectionName);
		else
			return true;
	}

	private Query getIdQuery(Class<T> docClass, Object id) {
		String fieldIdName = extractNameOfFieldId(docClass);
		Criteria criteria = where(fieldIdName).is(id);
		return new Query(criteria);
	}

	private Update createUpdate(Map<String, Object> fieldValues) {
		Update update = new Update();
		fieldValues.forEach((k,v) -> update.set(k, v));
		return update;
	}

	private void validateIdValue(Object idValue) {
		if(idValue == null)
			throw new IllegalArgumentException("Invalid value for idValue: NULL.");
	}

	private String extractNameOfFieldId(Class<T> domainClass) {
		for (Field field : domainClass.getFields()) {
			if (field.isAnnotationPresent(Id.class))
				return field.getName();
		}
		throw new IllegalStateException("Invalid Class Doc Type "
				+ domainClass.getSimpleName()
				+ "! Must contains an annotated field with @Id");
	}
}
