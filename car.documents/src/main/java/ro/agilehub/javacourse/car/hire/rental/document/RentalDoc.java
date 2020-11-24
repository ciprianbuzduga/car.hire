package ro.agilehub.javacourse.car.hire.rental.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

@Document(value = "rental")
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

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public UserDoc getUser() {
		return user;
	}

	public void setUser(UserDoc user) {
		this.user = user;
	}

	public CarDoc getCar() {
		return car;
	}

	public void setCar(CarDoc car) {
		this.car = car;
	}

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public RentalStatusEnum getStatus() {
		return status;
	}

	public void setStatus(RentalStatusEnum status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RentalDoc other = (RentalDoc) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		return true;
	}

}
