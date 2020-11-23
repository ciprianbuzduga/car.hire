package ro.agilehub.javacourse.car.hire.rental.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import ro.agilehub.javacourse.car.hire.rental.document.RentalDoc;
import ro.agilehub.javacourse.car.hire.repo.DocumentPatchRepositoryCustom;

public interface RentalRepository extends MongoRepository<RentalDoc, String>,
	DocumentPatchRepositoryCustom {

	Page<RentalDoc> findAllByUserAndCarAndStatus(String userId, String carId,
			String status, Pageable pageable);

}
