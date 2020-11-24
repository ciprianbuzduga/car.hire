package ro.agilehub.javacourse.car.hire.fleet.service.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.exception.PatchException;
import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarStatusDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument.OpEnum;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.fleet.service.CarsService;

@Qualifier("carsServiceStub")
@Service
public class CarsServiceStub implements CarsService {

	private final List<CarResponseDTO> carStubs = new ArrayList<>();

	@Override
	public String addCar(CarRequestDTO carDTO) {
		int nextId = carStubs.size() + 1;
		CarResponseDTO car = new CarResponseDTO();
		car.setClazzCode(carDTO.getClazzCode());
		car.setFuel(carDTO.getFuel());
		car.setId(String.valueOf(nextId));
		car.setMake(carDTO.getMake());
		car.setMileage(carDTO.getMileage());
		car.setModel(carDTO.getModel());
		car.setStatus(CarStatusDTO.ACTIVE);
		car.setYear(carDTO.getYear());
		carStubs.add(car);
		return car.getId();
	}

	@Override
	public boolean deleteCar(String id) {
		CarResponseDTO car = getCar(id);
		car.setStatus(CarStatusDTO.DELETED);
		//Should I remove from all cars?
		return carStubs.remove(car);
	}

	@Override
	public CarResponseDTO getCar(String id) {
		return carStubs.stream().filter(u -> u.getId().equals(id))
				.findFirst().orElseThrow(() ->
				new NoSuchElementException("No car found with id " + id));
	}

	@Override
	public PageCars findAll(Integer page, Integer size, String sort,
			String status) {
		List<CarResponseDTO> cars = null;
		if(status != null) {
			CarStatusDTO statusObj = CarStatusDTO.fromValue(status);
			cars = carStubs.stream().filter(c -> statusObj.equals(c.getStatus()))
				.collect(Collectors.toList());
		} else
			cars = carStubs;

		int totalNoCars = carStubs.size();
		List<List<CarResponseDTO>> listCars = new ArrayList<>();
		for(int i = 0; i < totalNoCars; i = i + size) {
			int limit = i + size;
			listCars.add(cars.subList(i, limit > totalNoCars ?
					totalNoCars : limit));
		}

		List<CarResponseDTO> finalList = listCars.size() > page ? listCars.get(page)
				: Collections.emptyList();

		PageCars pageCars = new PageCars();
		pageCars.setCurrentPage(page);
		pageCars.setPageSize(finalList.size());
		pageCars.setTotalNoRecords(totalNoCars);
		pageCars.setTotalPages(listCars.size());
		pageCars.setCars(finalList);
		return pageCars;
	}

	@Override
	public boolean updateCar(String id, List<PatchDocument> patchDocuments) {
		PatchDocument patchInvalid = patchDocuments.stream()
				.filter(p -> p != null && !OpEnum.REPLACE.equals(p.getOp()))
				.findFirst().orElse(null);
		if(patchInvalid != null)
			throw new PatchException("Only 'replace' operation is supported at the moment!");

		CarResponseDTO car = getCar(id);
		for(PatchDocument patch : patchDocuments)
			if(!applyPatch(patch, car))
				return false;
		return true;
	}

	private boolean applyPatch(PatchDocument patch, CarResponseDTO car) {
		String path = patch.getPath();

		Pattern pattern = Pattern.compile("/\\w+");
		Matcher matcher = pattern.matcher(path);
		if(!matcher.matches())
			throw new PatchException("Path is invalid! An expression "
					+ "'/<carAttr>' is accepted.");

		String carAttribute = path.replace("/", "");
		Object value = patch.getValue();

		try {
			if("id".equals(carAttribute))
				throw new PatchException("Invalid attribute 'id' for update.");
			if("status".equals(carAttribute))
				value = CarStatusDTO.fromValue((String) value);

			Field field = car.getClass().getDeclaredField(carAttribute);
			field.setAccessible(true);
			field.set(car, value);
			return true;
		} catch (NoSuchFieldException e) {
			throw new PatchException("Object Car doesn't have field '"
					+ e.getMessage() + "'", e.getMessage());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassCastException e) {
			throw new PatchException(e.getMessage(), carAttribute);
		}
		return false;
	}

	@Override
	public CarDoc getCarDoc(String carId) {
		return null;
	}

}
