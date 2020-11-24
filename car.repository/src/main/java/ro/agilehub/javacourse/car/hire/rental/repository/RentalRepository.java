package ro.agilehub.javacourse.car.hire.rental.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import ro.agilehub.javacourse.car.hire.patch.repository.DocumentPatchRepository;
import ro.agilehub.javacourse.car.hire.rental.document.RentalDoc;

public interface RentalRepository extends MongoRepository<RentalDoc, String>,
	DocumentPatchRepository<RentalDoc, String> {

	Page<RentalDoc> findAllByUserAndCarAndStatus(String userId, String carId,
			String status, Pageable pageable);

}
