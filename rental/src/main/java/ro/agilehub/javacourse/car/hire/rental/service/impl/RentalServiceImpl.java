package ro.agilehub.javacourse.car.hire.rental.service.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.common.PatchMapper;
import ro.agilehub.javacourse.car.hire.api.model.PageRentals;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.RentalRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.RentalResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.RentalStatusDTO;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.fleet.service.CarsService;
import ro.agilehub.javacourse.car.hire.rental.document.RentalDoc;
import ro.agilehub.javacourse.car.hire.rental.document.RentalStatusEnum;
import ro.agilehub.javacourse.car.hire.rental.repository.RentalRepository;
import ro.agilehub.javacourse.car.hire.rental.service.RentalService;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;
import ro.agilehub.javacourse.car.hire.user.service.UsersService;

@Service
public class RentalServiceImpl implements RentalService {

	private final RentalRepository rentalRepository;
	private final CarsService carsService;
	private final UsersService usersService;

	public RentalServiceImpl(RentalRepository rentalRepository,
			CarsService carsService,
			UsersService usersService) {
		this.rentalRepository = rentalRepository;
		this.carsService = carsService;
		this.usersService = usersService;
	}

	@Override
	public boolean deleteRental(String id) {
		RentalDoc rental = getRentalDoc(id);
		try {
			rentalRepository.delete(rental);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private RentalDoc getRentalDoc(String id) {
		return rentalRepository.findById(id).orElseThrow(
				() -> new NoSuchElementException("No rental found with id " + id));
	}

	@Override
	public String createRental(RentalRequestDTO rentalDTO) {
		RentalDoc rent = new RentalDoc();
		String carId = rentalDTO.getCarId();
		CarDoc car = carsService.getCarDoc(carId);
		rent.setCar(car);
		String userId = rentalDTO.getUserId();
		UserDoc user = usersService.getUserDoc(userId);
		rent.setUser(user);
		rent.setEndDate(rentalDTO.getEndDate().toLocalDateTime());
		rent.setStartDate(rentalDTO.getStartDate().toLocalDateTime());
		rent.setStatus(RentalStatusEnum.ACTIVE);
		try {
			rent = rentalRepository.save(rent);
			return rent.get_id();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public RentalResponseDTO getRental(String id) {
		RentalDoc rentDoc = getRentalDoc(id);
		return mapRentalDTO(rentDoc);
	}

	private RentalResponseDTO mapRentalDTO(RentalDoc rentDoc) {
		RentalResponseDTO rentResp = new RentalResponseDTO();
		Optional.ofNullable(rentDoc.getCar()).ifPresent(
				car -> rentResp.setCar(carsService.mapCarDTO(car)));
		rentResp.setEndDate(OffsetDateTime.of(rentDoc.getEndDate(), ZoneOffset.UTC));
		rentResp.setId(rentDoc.get_id());
		rentResp.setStartDate(OffsetDateTime.of(rentDoc.getStartDate(), ZoneOffset.UTC));
		rentResp.setStatus(RentalStatusDTO.fromValue(rentDoc.getStatus().getValue()));
		Optional.ofNullable(rentDoc.getUser()).ifPresent(
				user -> rentResp.setUser(usersService.mapUserDTO(user)));
		return rentResp;
	}

	@Override
	public PageRentals findAll(Integer page, Integer size, String sort,
			String userId, String carId, String status) {
		Pageable pageable = PageRequest.of(page, size);
		Page<RentalDoc> pageRentDocs = rentalRepository
				.findAllByUserAndCarAndStatus(userId, carId, status, pageable);
		PageRentals pageRentals = new PageRentals();
		pageRentals.currentPage(page)
			.pageSize(pageRentDocs.getNumberOfElements())
			.totalNoRecords((int) pageRentDocs.getTotalElements())
			.totalPages(pageRentDocs.getTotalPages());
		if(pageRentDocs.hasContent())
			pageRentDocs.forEach(rent -> pageRentals
					.addRentalsItem(mapRentalDTO(rent)));
		return pageRentals;
	}

	@Override
	public boolean updateRental(String id, List<PatchDocument> patchDocuments) {
		PatchMapper patchMapper = PatchMapper.getPatchMapper(patchDocuments, RentalDoc.class);
		return rentalRepository.updateDoc(RentalDoc.class, id, patchMapper.getFieldValues());
	}

}
