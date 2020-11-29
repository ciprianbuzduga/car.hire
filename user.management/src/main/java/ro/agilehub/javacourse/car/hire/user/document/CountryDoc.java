package ro.agilehub.javacourse.car.hire.user.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "country")
@Data
@EqualsAndHashCode(of = "_id")
public class CountryDoc {

	@Id
	private String _id;

	private String name;

	private String isoCode;

	private CountryStatusEnum status;

}
