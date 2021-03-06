package ro.agilehub.javacourse.car.hire.user.document;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Document(value = UserDoc.COLLECTION_NAME)
@Data
@EqualsAndHashCode(of = "_id")
public class UserDoc {
	public static final String COLLECTION_NAME = "user";

	@Id
	private String _id;

	private String password;

	@Indexed(name = "users_email_idx_uq", unique = true)
	private String email;

	@Indexed(name = "users_username_idx_uq", unique = true)
	private String username;

	private String firstName;

	private String lastName;

	private String country;

	private String driverLicenseNo;

	private UserStatusEnum status;

	private String title;

	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String lastModifiedBy;

	@CreatedDate
	private Date createdDate;

	@LastModifiedDate
	private Date lastModifiedDate;

}
