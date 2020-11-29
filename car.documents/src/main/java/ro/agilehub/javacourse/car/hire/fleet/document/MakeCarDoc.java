package ro.agilehub.javacourse.car.hire.fleet.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = "makeCar")
@Data
@EqualsAndHashCode(of = "_id")
public class MakeCarDoc {

	@Id
	private Integer _id;

	private String name;

}
