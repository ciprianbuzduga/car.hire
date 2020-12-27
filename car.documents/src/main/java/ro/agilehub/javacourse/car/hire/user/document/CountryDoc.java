package ro.agilehub.javacourse.car.hire.user.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(collection = CountryDoc.COLLECTION_NAME)
@Data
@EqualsAndHashCode(of = "_id")
public class CountryDoc {

	public static final String COLLECTION_NAME = "country";

	@Id
	private String _id;

	@Indexed(name = "country_name_idx_uq", unique = true)
	private String name;

	@Indexed(name = "country_isoCode_idx_uq", unique = true)
	private String isoCode;

	private CountryStatusEnum status;

}
