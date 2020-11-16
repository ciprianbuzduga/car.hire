package ro.agilehub.javacourse.car.hire.rental.service;

import java.util.List;

import ro.agilehub.javacourse.car.hire.api.model.PageRentals;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.RentalDTO;

public interface RentalService {

	boolean cancelRental(Integer id);

	boolean createRental(RentalDTO rentalDTO);

	RentalDTO getRental(Integer id);

	PageRentals findAll(Integer page, Integer size, String sort, Integer userId,
			Integer carId, String status);

	boolean updateRental(Integer id, List<PatchDocument> patchDocuments);

}
