package ro.agilehub.javacourse.car.hire.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ro.agilehub.javacourse.car.hire.patch.repository.DocumentPatchRepository;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

public interface UserRepository extends MongoRepository<UserDoc, String>,
	DocumentPatchRepository<UserDoc, String> {

	long countByEmail(String emal);

	long countByUsername(String username);
}
