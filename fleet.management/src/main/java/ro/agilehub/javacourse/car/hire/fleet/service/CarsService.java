package ro.agilehub.javacourse.car.hire.fleet.service;

import java.util.List;
import java.util.Optional;

import ro.agilehub.javacourse.car.hire.api.model.CarClazzCodeDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarStatusDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;

public interface CarsService {

	String addCar(CarRequestDTO carDTO);

	boolean deleteCar(String id);

	CarResponseDTO getCar(String id);

	PageCars findAll(Integer page, Integer size, String sort, String status);

	boolean updateCar(String id, List<PatchDocument> patchDocuments);

	CarDoc getCarDoc(String carId);

	default CarResponseDTO mapCarDTO(CarDoc carDoc) {
		CarResponseDTO carDTO = new CarResponseDTO();
		carDTO.setClazzCode(CarClazzCodeDTO.fromValue(carDoc.getClazzCode().getValue()));
		carDTO.setFuel(carDoc.getFuel());
		carDTO.setId(carDoc.get_id());
		Optional.ofNullable(carDoc.getMakeCar())
			.ifPresent(m -> carDTO.setMake(m.getName()));
		carDTO.setMileage(carDoc.getMileage());
		carDTO.setModel(carDoc.getModel());
		carDTO.setStatus(CarStatusDTO.fromValue(carDoc.getStatus().getValue()));
		carDTO.setYear(carDoc.getYear());
		return carDTO;
	}
}
