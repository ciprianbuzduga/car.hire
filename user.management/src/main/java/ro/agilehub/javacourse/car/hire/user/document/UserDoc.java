package ro.agilehub.javacourse.car.hire.user.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "user")
@Data
@EqualsAndHashCode(of = "_id")
public class UserDoc {

	@Id
	private String _id;

	private String password;

	private String email;

	private String username;

	private String firstName;

	private String lastName;

	private String country;

	private String driverLicenseNo;

	private UserStatusEnum status;

	private String title;

}
