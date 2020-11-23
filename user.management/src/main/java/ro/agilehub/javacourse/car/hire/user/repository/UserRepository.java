package ro.agilehub.javacourse.car.hire.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ro.agilehub.javacourse.car.hire.repo.DocumentPatchRepositoryCustom;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

public interface UserRepository extends MongoRepository<UserDoc, String>,
	DocumentPatchRepositoryCustom {

}
