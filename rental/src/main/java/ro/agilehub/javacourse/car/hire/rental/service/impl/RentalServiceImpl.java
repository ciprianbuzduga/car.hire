package ro.agilehub.javacourse.car.hire.rental.service.impl;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.common.PatchMapper;
import ro.agilehub.javacourse.car.hire.api.model.PageRentals;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.RentalRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.RentalResponseDTO;
import ro.agilehub.javacourse.car.hire.rental.document.RentalDoc;
import ro.agilehub.javacourse.car.hire.rental.mapper.RentalMapper;
import ro.agilehub.javacourse.car.hire.rental.repository.RentalRepository;
import ro.agilehub.javacourse.car.hire.rental.service.RentalService;

@Service
public class RentalServiceImpl implements RentalService {

	private final RentalRepository rentalRepository;
	private final RentalMapper rentalMapper;

	public RentalServiceImpl(RentalRepository rentalRepository,
			RentalMapper rentalMapper) {
		this.rentalRepository = rentalRepository;
		this.rentalMapper = rentalMapper;
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
		RentalDoc rent = rentalMapper.mapToRentalDoc(rentalDTO);
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
		return rentalMapper.mapToRentalResponseDTO(rentDoc);
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
			pageRentDocs.forEach(rent -> pageRentals.addRentalsItem(
							rentalMapper.mapToRentalResponseDTO(rent)));
		return pageRentals;
	}

	@Override
	public boolean updateRental(String id, List<PatchDocument> patchDocuments) {
		PatchMapper patchMapper = PatchMapper.getPatchMapper(patchDocuments, RentalDoc.class);
		return rentalRepository.updateDoc(RentalDoc.class, id, patchMapper.getFieldValues());
	}

}
