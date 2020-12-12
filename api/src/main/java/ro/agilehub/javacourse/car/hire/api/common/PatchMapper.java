package ro.agilehub.javacourse.car.hire.api.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.reflect.FieldUtils;

import lombok.RequiredArgsConstructor;
import ro.agilehub.javacourse.car.hire.api.exception.PatchException;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument.OpEnum;

@RequiredArgsConstructor
public class PatchMapper {
	private static final Pattern PATTERN_WORDS = Pattern.compile("/\\w+");

	private final PatchHolder patchHolder;

	private static class PatchHolder {
		final Class<?> ownerFields;
		final Map<String, Object> fieldValues = new HashMap<>();
	
		private PatchHolder(Class<?> ownerClass) {
			this.ownerFields = ownerClass;
		}
	}

	public Class<?> getOwnerFields() {
		return patchHolder.ownerFields;
	}

	public Map<String, Object> getFieldValues() {
		return patchHolder.fieldValues;
	}

	public static PatchMapper getPatchMapper(
			List<PatchDocument> patchDocuments,
			Class<?> ownerClass) {
		validatePatchDocuments(patchDocuments);
		PatchHolder patchHolder = new PatchHolder(ownerClass);
		List<Field> allFields = FieldUtils.getAllFieldsList(ownerClass);
		String ownerName = ownerClass.getSimpleName();
		for(PatchDocument patch : patchDocuments) {
			String path = patch.getPath();

			Matcher matcher = PATTERN_WORDS.matcher(path);
			if(!matcher.matches())
				throw new PatchException("Path is invalid! An expression "
						+ "'/" + ownerName +".fieldName' is accepted.");

			String fieldToModify = path.replace("/", "");
			allFields.stream().filter(f -> f.getName().equals(fieldToModify))
				.findFirst().orElseThrow(
						() -> new PatchException("Field '" + fieldToModify
									+ "' not found in doc type " + ownerName,
									fieldToModify));
			Object newValue = patch.getValue();
			patchHolder.fieldValues.put(fieldToModify, newValue);
		}
		return new PatchMapper(patchHolder);
	}

	private static void validatePatchDocuments(List<PatchDocument> patchDocuments) {
		PatchDocument patchInvalid = patchDocuments.stream()
				.filter(p -> p != null && !OpEnum.REPLACE.equals(p.getOp()))
				.findFirst().orElse(null);
		if(patchInvalid != null)
			throw new PatchException("Only 'replace' operation is supported at the moment!");
	}
}
