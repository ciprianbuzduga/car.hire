package ro.agilehub.javacourse.car.hire.fleet.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.fleet.document.MakeCarDoc;
import ro.agilehub.javacourse.car.hire.fleet.repository.MakeCarRepository;

import java.util.NoSuchElementException;

public abstract class CarMapperDecorator implements CarMapper {

	@Autowired
	@Qualifier("delegate")
	private CarMapper delegate;

	@Autowired
	private MakeCarRepository makeCarRepository;

	@Override
	public CarDoc mapToCarDoc(CarRequestDTO carDTO) {
		String make = carDTO.getMake();
		MakeCarDoc makeDoc = makeCarRepository.findByName(make);
		if(makeDoc == null)
			throw new NoSuchElementException("No makeCar found with name " + make);
		CarDoc carDoc = delegate.mapToCarDoc(carDTO);
		carDoc.setMakeCar(makeDoc);
		return carDoc;
	}

}
