package ro.agilehub.javacourse.car.hire.fleet.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "makeCar")
@Data
@EqualsAndHashCode(of = "_id")
public class MakeCarDoc {

	@Id
	private Integer _id;

	private String name;

}
