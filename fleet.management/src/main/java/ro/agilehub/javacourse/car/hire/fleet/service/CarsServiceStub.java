package ro.agilehub.javacourse.car.hire.fleet.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.exception.PatchException;
import ro.agilehub.javacourse.car.hire.api.model.CarDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarDTO.StatusEnum;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument.OpEnum;

@Service
public class CarsServiceStub implements CarsService {

	private final List<CarDTO> carStubs = new ArrayList<>();

	@Override
	public boolean addCar(CarDTO carDTO) {
		int nextId = carStubs.size() + 1;
		carDTO.setId(nextId);
		return carStubs.add(carDTO);
	}

	@Override
	public boolean deleteCar(Integer id) {
		CarDTO car = getCar(id);
		car.setStatus(StatusEnum.DELETED);
		//Should I remove from all cars?
		return carStubs.remove(car);
	}

	@Override
	public CarDTO getCar(Integer id) {
		return carStubs.stream().filter(u -> u.getId().equals(id))
				.findFirst().orElseThrow(() ->
				new NoSuchElementException("No car found with id " + id));
	}

	@Override
	public PageCars findAll(Integer page, Integer size, String sort,
			String status) {
		StatusEnum statusObj = StatusEnum.fromValue(status);
		List<CarDTO> cars = null;
		if(statusObj != null)
			cars = carStubs.stream().filter(c -> statusObj.equals(c.getStatus()))
				.collect(Collectors.toList());
		else
			cars = carStubs;

		int totalNoCars = carStubs.size();
		List<List<CarDTO>> listCars = new ArrayList<>();
		for(int i = 0; i < totalNoCars; i = i + size) {
			int limit = i + size;
			listCars.add(cars.subList(i, limit > totalNoCars ?
					totalNoCars : limit));
		}

		List<CarDTO> finalList = listCars.get(page);

		PageCars pageCars = new PageCars();
		pageCars.setCurrentPage(page);
		pageCars.setPageSize(finalList.size());
		pageCars.setTotalNoCars(totalNoCars);
		pageCars.setTotalPages(listCars.size());
		pageCars.setCars(finalList);
		return pageCars;
	}

	@Override
	public boolean updateCar(Integer id, List<PatchDocument> patchDocuments) {
		PatchDocument patchInvalid = patchDocuments.stream()
				.filter(p -> p != null && !OpEnum.REPLACE.equals(p.getOp()))
				.findFirst().orElse(null);
		if(patchInvalid != null)
			throw new PatchException("Only 'replace' operation is supported at the moment!");

		CarDTO car = getCar(id);
		for(PatchDocument patch : patchDocuments)
			if(!applyPatch(patch, car))
				return false;
		return true;
	}

	private boolean applyPatch(PatchDocument patch, CarDTO car) {
		String path = patch.getPath();

		Pattern pattern = Pattern.compile("/\\w+");
		Matcher matcher = pattern.matcher(path);
		if(!matcher.matches())
			throw new PatchException("Path is invalid! An expression "
					+ "'/<carAttr>' is accepted.");

		String userAttribute = path.replace("/", "");
		Object value = patch.getValue();

		try {
			if("id".equals(userAttribute))
				throw new PatchException("Invalid attribute 'id' for update.");
			if("status".equals(userAttribute))
				value = StatusEnum.fromValue((String) value);

			Field field = car.getClass().getDeclaredField(userAttribute);
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
			throw new PatchException(e.getMessage(), userAttribute);
		}
		return false;
	}

}
