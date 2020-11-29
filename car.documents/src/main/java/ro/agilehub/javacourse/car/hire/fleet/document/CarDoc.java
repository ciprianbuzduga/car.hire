package ro.agilehub.javacourse.car.hire.fleet.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "car")
@Data
@EqualsAndHashCode(of = "_id")
public class CarDoc {

	@Id
	private String _id;

	@DBRef
	private MakeCarDoc makeCar;

	private String model;

	private Integer year;

	private Integer mileage;

	private String fuel;

	private CarClazzEnum clazzCode;

	private CarStatusEnum status;

}
