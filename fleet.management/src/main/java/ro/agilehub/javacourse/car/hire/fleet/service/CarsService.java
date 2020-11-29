package ro.agilehub.javacourse.car.hire.fleet.service;

import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;

import java.util.List;

public interface CarsService {

	String addCar(CarRequestDTO carDTO);

	boolean deleteCar(String id);

	CarResponseDTO getCar(String id);

	PageCars findAll(Integer page, Integer size, String sort, String status);

	boolean updateCar(String id, List<PatchDocument> patchDocuments);

	CarDoc getCarDoc(String carId);

}
