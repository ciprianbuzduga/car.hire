package ro.agilehub.javacourse.car.hire.repo.custom;

import java.util.List;

import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;

public interface DocumentPatchRepositoryCustom {

	boolean updateDoc(List<PatchDocument> patchDocuments, Class<?> docClass,
			Object idValue);
}
