package ro.agilehub.javacourse.car.hire.fleet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import ro.agilehub.javacourse.car.hire.fleet.document.CarClazzDoc;

public interface CarClazzRepository extends MongoRepository<CarClazzDoc, Integer>{

	CarClazzDoc findByCode(String code);
}
