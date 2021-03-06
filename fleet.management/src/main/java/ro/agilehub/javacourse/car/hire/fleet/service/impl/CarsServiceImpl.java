package ro.agilehub.javacourse.car.hire.fleet.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ro.agilehub.javacourse.car.hire.api.common.PatchMapper;
import ro.agilehub.javacourse.car.hire.api.exception.DuplicateKeyErrorCollection;
import ro.agilehub.javacourse.car.hire.api.exception.EntityAlreadyExistsException;
import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.fleet.document.MakeCarDoc;
import ro.agilehub.javacourse.car.hire.fleet.mapper.CarMapper;
import ro.agilehub.javacourse.car.hire.fleet.repository.CarRepository;
import ro.agilehub.javacourse.car.hire.fleet.repository.MakeCarRepository;
import ro.agilehub.javacourse.car.hire.fleet.service.CarsService;

@Service
@RequiredArgsConstructor
public class CarsServiceImpl implements CarsService {

	private final CarRepository carRepository;
	private final MakeCarRepository makeCarRepository;
	private final CarMapper carMapper;

	@Override
	public String addCar(CarRequestDTO carDTO) {
		String registrationNo = carDTO.getRegistrationNo();
		long countCars = carRepository.countByRegistrationNo(registrationNo);
		if(countCars > 0)
			throw new EntityAlreadyExistsException(CarDoc.COLLECTION_NAME,
					"registrationNo", registrationNo);

		String make = carDTO.getMake();
		MakeCarDoc makeDoc = makeCarRepository.findByName(make);
		if(makeDoc == null)
			throw new NoSuchElementException("No makeCar found with name " + make);
		CarDoc carDoc = carMapper.mapToCarDoc(carDTO);
		carDoc.setMakeCar(makeDoc);
		try {
			carDoc = carRepository.save(carDoc);
			return carDoc.get_id();
		} catch (DuplicateKeyException e) {
			String message = e.getCause().getMessage();
			System.err.println("Unique index constraint violation: " + message);
			throw new DuplicateKeyErrorCollection(message);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean deleteCar(String id) {
		CarDoc car = getCarDoc(id);
		try {
			carRepository.delete(car);
			return true;
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public CarDoc getCarDoc(String id) {
		return carRepository.findById(id).orElseThrow(
				() -> new NoSuchElementException("No car found with id " + id));
	}

	@Override
	public CarResponseDTO getCar(String id) {
		CarDoc carDoc = getCarDoc(id);
		return carMapper.mapToCarResponseDTO(carDoc);
	}

	@Override
	public PageCars findAll(Integer page, Integer size, String sort, String status) {
		Pageable pageable = PageRequest.of(page, size);
		Page<CarDoc> pageCarsDoc = carRepository.findAllByStatus(status, pageable);
		PageCars pageCars = new PageCars();
		pageCars.currentPage(page)
			.pageSize(pageCarsDoc.getNumberOfElements())
			.totalNoRecords((int) pageCarsDoc.getTotalElements())
			.totalPages(pageCarsDoc.getTotalPages());
		if(pageCarsDoc.hasContent())
			pageCarsDoc.forEach(car -> pageCars.addCarsItem(
					carMapper.mapToCarResponseDTO(car)));
		return pageCars;
	}

	@Override
	public boolean updateCar(String id, List<PatchDocument> patchDocuments) {
		//TODO registrationNo validation
		PatchMapper patchMapper = PatchMapper.getPatchMapper(patchDocuments, CarDoc.class);
		boolean result = false;
		try {
			result = carRepository.updateDoc(CarDoc.class, id, patchMapper.getFieldValues());
		} catch (DuplicateKeyException e) {
			String message = e.getCause().getMessage();
			System.err.println("Unique index constraint violation: " + message);
			throw new DuplicateKeyErrorCollection(message);
		}
		return result;
	}

}
