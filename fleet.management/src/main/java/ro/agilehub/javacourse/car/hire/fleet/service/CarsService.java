package ro.agilehub.javacourse.car.hire.fleet.service;

import java.util.List;

import ro.agilehub.javacourse.car.hire.api.model.CarDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;

public interface CarsService {

	boolean addCar(CarDTO carDTO);

	boolean deleteCar(Integer id);

	CarDTO getCar(Integer id);

	PageCars findAll(Integer page, Integer size, String sort, String status);

	boolean updateCar(Integer id, List<PatchDocument> patchDocuments);

}
