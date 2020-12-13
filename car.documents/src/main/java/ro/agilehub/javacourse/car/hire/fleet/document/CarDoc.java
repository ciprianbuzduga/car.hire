package ro.agilehub.javacourse.car.hire.fleet.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = CarDoc.COLLECTION_NAME)
@Data
@EqualsAndHashCode(of = "_id")
public class CarDoc {
	public static final String COLLECTION_NAME = "car";

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

	@Indexed(name = "car_registrationNo_idx_uq", unique = true)
	private String registrationNo;
}
