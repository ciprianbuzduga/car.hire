package ro.agilehub.javacourse.car.hire.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ro.agilehub.javacourse.car.hire.patch.repository.DocumentPatchRepository;
import ro.agilehub.javacourse.car.hire.user.document.CountryDoc;

public interface CountryRepository extends MongoRepository<CountryDoc, String>,
	DocumentPatchRepository<CountryDoc, String> {

	long countByIsoCodeIgnoreCase(String isoCode);

	long countByNameIgnoreCase(String name);

}
