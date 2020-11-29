package ro.agilehub.javacourse.car.hire.rental.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ro.agilehub.javacourse.car.hire.api.model.RentalRequestDTO;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.fleet.service.CarsService;
import ro.agilehub.javacourse.car.hire.rental.document.RentalDoc;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;
import ro.agilehub.javacourse.car.hire.user.service.UsersService;

public abstract class RentalMapperDecorator implements RentalMapper {

	@Autowired
	@Qualifier("delegate")
	private RentalMapper delegate;

	@Autowired
	private UsersService usersService;

	@Autowired
	private CarsService carsService;

	@Override
	public RentalDoc mapToRentalDoc(RentalRequestDTO rentalDTO) {
		RentalDoc rentDoc = delegate.mapToRentalDoc(rentalDTO);
		String carId = rentalDTO.getCarId();
		CarDoc car = carsService.getCarDoc(carId);
		rentDoc.setCar(car);
		String userId = rentalDTO.getUserId();
		UserDoc user = usersService.getUserDoc(userId);
		rentDoc.setUser(user);
		return rentDoc;
	}
}
