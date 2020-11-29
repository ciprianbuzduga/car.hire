package ro.agilehub.javacourse.car.hire.rental.document;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

import java.time.LocalDateTime;

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

}
