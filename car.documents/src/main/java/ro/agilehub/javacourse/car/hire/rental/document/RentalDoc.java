package ro.agilehub.javacourse.car.hire.rental.document;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

@Document(value = "rental")
@Data
@EqualsAndHashCode(of = "_id")
public class RentalDoc {

	@Id
	private String _id;

	@DBRef
	private UserDoc user;

	@DBRef
	private CarDoc car;

	private LocalDateTime startDate;

	private LocalDateTime endDate;

	private RentalStatusEnum status;

	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String lastModifiedBy;

	@CreatedDate
	private Date createdDate;

	@LastModifiedDate
	private Date lastModifiedDate;

}
