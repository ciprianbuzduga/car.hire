package ro.agilehub.javacourse.car.hire.fleet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.patch.repository.DocumentPatchRepository;

public interface CarRepository extends MongoRepository<CarDoc, String>,
	DocumentPatchRepository<CarDoc, String> {

	Page<CarDoc> findAllByStatus(String status, Pageable pageable);

	long countByRegistrationNo(String registrationNo);

}
