package ro.agilehub.javacourse.car.hire.rental.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import ro.agilehub.javacourse.car.hire.api.model.PageRentals;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.RentalRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.RentalResponseDTO;
import ro.agilehub.javacourse.car.hire.api.specification.RentalsApi;
import ro.agilehub.javacourse.car.hire.rental.service.RentalService;

@RestController
public class RentalController implements RentalsApi {

	private final RentalService rentalService;

	public RentalController(RentalService rentalService) {
		this.rentalService = rentalService;
	}

	@Override
	public ResponseEntity<Void> deleteRental(String id) {
		boolean deleted = rentalService.deleteRental(id);
		if(deleted) {
			return ResponseEntity.noContent().build();
		} else 
			throw new ServerErrorException("Cannot cancel the reservation " + id
					+ " because of unknown reasone", (Throwable)null);
	}

	@Override
	public ResponseEntity<Void> createRental(@Valid RentalRequestDTO rentalDTO) {
		String newId = rentalService.createRental(rentalDTO);
		if(newId != null) {
			UriComponents uriComponents = UriComponentsBuilder.newInstance()
					.scheme("http").host("localhost").port(8080)
					.path("/rentals/{id}").buildAndExpand(newId);
			return ResponseEntity.created(uriComponents.toUri()).build();
		} else
			throw new ServerErrorException("Cannot create the reservation "
					+ "because of unknown reasone", (Throwable)null);
	}

	@Override
	public ResponseEntity<RentalResponseDTO> getRental(String id) {
		RentalResponseDTO rental = rentalService.getRental(id);
		return ResponseEntity.ok(rental);
	}

	@Override
	public ResponseEntity<PageRentals> getRentals(@Min(0) @Valid Integer page,
			@Min(1) @Valid Integer size,
			@Valid String sort, @Valid String userId, @Valid String carId,
			@Valid String status) {
		PageRentals pageRentals = rentalService.findAll(page, size, sort, userId,
				carId, status);
		return ResponseEntity.ok(pageRentals);
	}

	@Override
	public ResponseEntity<Void> updateRental(String id, @Valid List<PatchDocument> patchDocument) {
		boolean update = rentalService.updateRental(id, patchDocument);
		if(update)
			return ResponseEntity.noContent().build();
		else
			throw new ServerErrorException("Cannot update the rental " + id
					+ " because of unknown reasone", (Throwable)null);
	}
}
