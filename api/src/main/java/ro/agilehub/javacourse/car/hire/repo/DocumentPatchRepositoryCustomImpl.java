package ro.agilehub.javacourse.car.hire.repo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.client.result.UpdateResult;

import ro.agilehub.javacourse.car.hire.api.exception.PatchException;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument.OpEnum;

public class DocumentPatchRepositoryCustomImpl implements DocumentPatchRepositoryCustom {

	private final MongoTemplate template;

	public DocumentPatchRepositoryCustomImpl(MongoTemplate template) {
		this.template = template;
	}

	@Override
	public boolean updateDoc(List<PatchDocument> patchDocuments, Class<?> docClass,
			Object idValue) {
		validateIdValue(idValue);
		validatePatchDocuments(patchDocuments);

		Query query = createQuery(docClass, idValue);
		String docName = docClass.getSimpleName();
		Update update = createUpdate(patchDocuments, docClass, docName);
		UpdateResult ur = template.updateFirst(query, update, docClass);
		long matchedCount = ur.getMatchedCount();
		if(matchedCount == 0)
			throw new NoSuchElementException("No document found with id "
					+ idValue + " for type " + docName);
		else
			return true;
	}

	private Query createQuery(Class<?> docClass, Object idValue) {
		String fieldIdName = extractNameOfFieldId(docClass);
		Criteria criteria = Criteria.where(fieldIdName).is(idValue);
		return Query.query(criteria);
	}

	private Update createUpdate(List<PatchDocument> patchDocuments,
			Class<?> docClass, String docName) {
		List<Field> allFields = FieldUtils.getAllFieldsList(docClass);
		Update update = new Update();
		for(PatchDocument patch: patchDocuments) {
			String path = patch.getPath();

			Pattern pattern = Pattern.compile("/\\w+");
			Matcher matcher = pattern.matcher(path);
			if(!matcher.matches())
				throw new PatchException("Path is invalid! An expression "
						+ "'/" + docName +".fieldName' is accepted.");

			String fieldToModify = path.replace("/", "");
			allFields.stream().filter(f -> f.getName().equals(fieldToModify))
				.findFirst().orElseThrow(
						() -> new PatchException("Field '" + fieldToModify
									+ "' not found in doc type " + docName,
									fieldToModify));
			Object newValue = patch.getValue();
			//some validators here
			
			update.set(fieldToModify, newValue);
		}
		return update;
	}

	private void validatePatchDocuments(List<PatchDocument> patchDocuments) {
		PatchDocument patchInvalid = patchDocuments.stream()
				.filter(p -> p != null && !OpEnum.REPLACE.equals(p.getOp()))
				.findFirst().orElse(null);
		if(patchInvalid != null)
			throw new PatchException("Only 'replace' operation is supported at the moment!");
	}

	private void validateIdValue(Object idValue) {
		if(idValue == null)
			throw new PatchException("Invalid value for idValue: NULL.");
	}

	private String extractNameOfFieldId(Class<?> docClass) {
		Field fieldId = FieldUtils.getFieldsListWithAnnotation(docClass,
				Id.class).stream().findFirst().orElseThrow(
						() -> new PatchException("Invalid Class Doc Type "
								+ docClass.getSimpleName()
								+ "! Must contains an annotated field with @Id"));
		return fieldId.getName();
	}

}
