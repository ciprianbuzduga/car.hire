package ro.agilehub.javacourse.car.hire.fleet.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ro.agilehub.javacourse.car.hire.fleet.document.MakeCarDoc;

public interface MakeCarRepository extends MongoRepository<MakeCarDoc, Integer>{

	MakeCarDoc findByName(String name);

}
