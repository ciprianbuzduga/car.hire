package ro.agilehub.javacourse.car.hire.fleet.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.specification.CarsApi;
import ro.agilehub.javacourse.car.hire.fleet.service.CarsService;

@RestController
public class CarsController implements CarsApi {

	private final CarsService carsService;

	public CarsController(@Qualifier("carsService") CarsService carsService) {
		this.carsService = carsService;
	}

	@Override
	public ResponseEntity<Void> addCar(@Valid CarRequestDTO carDTO) {
		String newId = carsService.addCar(carDTO);
		if(newId != null) {
			UriComponents uriComponents = UriComponentsBuilder.newInstance()
					.scheme("http").host("localhost").port(8080)
					.path("/cars/{id}").buildAndExpand(newId);
			return ResponseEntity.created(uriComponents.toUri()).build();
		} else
			throw new ServerErrorException("Cannot add the car because of "
					+ "unknown reasone", (Throwable)null);
	}

	@Override
	public ResponseEntity<Void> deleteCar(String id) {
		boolean deleted = carsService.deleteCar(id);
		if(deleted) {
			return ResponseEntity.noContent().build();
		} else 
			throw new ServerErrorException("Cannot delete the car " + id
					+ " because of unknown reasone", (Throwable)null);
	}

	@Override
	public ResponseEntity<CarResponseDTO> getCar(String id) {
		CarResponseDTO car = carsService.getCar(id);
		return ResponseEntity.ok(car);
	}

	@Override
	public ResponseEntity<PageCars> getCars(@Min(0) @Valid Integer page,
			@Min(1) @Valid Integer size,
			@Valid String sort, @Valid String status) {
		PageCars pageCars = carsService.findAll(page, size, sort, status);
		return ResponseEntity.ok(pageCars);
	}

	@Override
	public ResponseEntity<Void> updateCar(String id,
			@Valid List<PatchDocument> patchDocument) {
		boolean update = carsService.updateCar(id, patchDocument);
		if(update)
			return ResponseEntity.noContent().build();
		else
			throw new ServerErrorException("Cannot update the car " + id
					+ " because of unknown reasone", (Throwable)null);
	}

}
