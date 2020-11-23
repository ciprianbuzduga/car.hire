package ro.agilehub.javacourse.car.hire.fleet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.repo.custom.DocumentPatchRepositoryCustom;

public interface CarRepository extends MongoRepository<CarDoc, String>,
	DocumentPatchRepositoryCustom {

	Page<CarDoc> findAllByStatus(String status, Pageable pageable);
}
