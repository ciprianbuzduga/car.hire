package ro.agilehub.javacourse.car.hire.fleet.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "car")
public class CarDoc {

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

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public MakeCarDoc getMakeCar() {
		return makeCar;
	}

	public void setMakeCar(MakeCarDoc makeCar) {
		this.makeCar = makeCar;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getMileage() {
		return mileage;
	}

	public void setMileage(Integer mileage) {
		this.mileage = mileage;
	}

	public String getFuel() {
		return fuel;
	}

	public void setFuel(String fuel) {
		this.fuel = fuel;
	}

	public CarClazzEnum getClazzCode() {
		return clazzCode;
	}

	public void setClazzCode(CarClazzEnum clazzCode) {
		this.clazzCode = clazzCode;
	}

	public CarStatusEnum getStatus() {
		return status;
	}

	public void setStatus(CarStatusEnum status) {
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
		CarDoc other = (CarDoc) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		return true;
	}

}
