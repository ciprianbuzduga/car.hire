package ro.agilehub.javacourse.car.hire.rental.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ro.agilehub.javacourse.car.hire.api.exception.PatchException;
import ro.agilehub.javacourse.car.hire.api.model.PageRentals;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument.OpEnum;
import ro.agilehub.javacourse.car.hire.api.model.RentalDTO;
import ro.agilehub.javacourse.car.hire.api.model.RentalDTO.StatusEnum;
import ro.agilehub.javacourse.car.hire.fleet.service.CarsService;
import ro.agilehub.javacourse.car.hire.user.service.UsersService;

@Service
public class RentalServiceStub implements RentalService {

	private final List<RentalDTO> rentalStubs = new ArrayList<>();

	private final UsersService usersService;
	private final CarsService carsService;

	public RentalServiceStub(CarsService carsService,
			UsersService usersService) {
		this.usersService = usersService;
		this.carsService = carsService;
	}

	@Override
	public boolean cancelRental(Integer id) {
		RentalDTO rental = getRental(id);
		rental.setStatus(StatusEnum.CANCELLED);
		//Should I remove from all list?
		return true;
	}

	@Override
	public boolean createRental(RentalDTO rentalDTO) {
		validateUserAndCarId(rentalDTO);

		int nextId = rentalStubs.size() + 1;
		rentalDTO.setId(nextId);
		return rentalStubs.add(rentalDTO);
	}

	private void validateUserAndCarId(RentalDTO rentalDTO) {
		Integer carId = rentalDTO.getCarId();
		carsService.getCar(carId);
		Integer userId = rentalDTO.getUserId();
		usersService.getUser(userId);
	}

	@Override
	public RentalDTO getRental(Integer id) {
		return rentalStubs.stream().filter(u -> u.getId().equals(id))
				.findFirst().orElseThrow(() ->
				new NoSuchElementException("No rental found with id " + id));
	}

	@Override
	public PageRentals findAll(Integer page, Integer size, String sort,
			Integer userId, Integer carId, String status) {
		List<RentalDTO> filteredRentals = null;
		if(rentalStubs.isEmpty())
			filteredRentals = Collections.emptyList();
		else
			filteredRentals = rentalStubs.stream()
				.filter(r -> userId != null ? r.getUserId().equals(userId) : true)
				.filter(r -> carId != null ? r.getCarId().equals(carId) : true)
				.filter(r -> status != null ?
					r.getStatus().equals(StatusEnum.fromValue(status)) : true)
				.collect(Collectors.toList());

		int totalNoRentals = rentalStubs.size();
		List<List<RentalDTO>> listRentals = new ArrayList<>();
		if(!filteredRentals.isEmpty())
			for(int i = 0; i < totalNoRentals; i = i + size) {
				int limit = i + size;
				listRentals.add(filteredRentals.subList(i,
						limit > totalNoRentals ? totalNoRentals : limit));
			}

		List<RentalDTO> finalList = listRentals.size() > page ? listRentals.get(page)
				: Collections.emptyList();

		PageRentals pageRentals = new PageRentals();
		pageRentals.setCurrentPage(page);
		pageRentals.setPageSize(finalList.size());
		pageRentals.setTotalNoRentals(totalNoRentals);
		pageRentals.setTotalPages(listRentals.size());
		pageRentals.setRentals(finalList);
		return pageRentals;
	}

	@Override
	public boolean updateRental(Integer id, List<PatchDocument> patchDocuments) {
		PatchDocument patchInvalid = patchDocuments.stream()
				.filter(p -> p != null && !OpEnum.REPLACE.equals(p.getOp()))
				.findFirst().orElse(null);
		if(patchInvalid != null)
			throw new PatchException("Only 'replace' operation is supported at the moment!");

		RentalDTO rental = getRental(id);
		for(PatchDocument patch : patchDocuments)
			if(!applyPatch(patch, rental))
				return false;
		return true;
	}

	private boolean applyPatch(PatchDocument patch, RentalDTO rental) {
		String path = patch.getPath();

		Pattern pattern = Pattern.compile("/\\w+");
		Matcher matcher = pattern.matcher(path);
		if(!matcher.matches())
			throw new PatchException("Path is invalid! An expression "
					+ "'/<rentalAttr>' is accepted.");

		String rentalAttribute = path.replace("/", "");
		Object value = patch.getValue();

		try {
			if("id".equals(rentalAttribute))
				throw new PatchException("Invalid attribute 'id' for update.");
			if("status".equals(rentalAttribute))
				value = StatusEnum.fromValue((String) value);
			if("userId".equals(rentalAttribute))
				usersService.getUser((Integer) value);
			if("carId".equals(rentalAttribute))
				carsService.getCar((Integer) value);

			Field field = rental.getClass().getDeclaredField(rentalAttribute);
			field.setAccessible(true);
			field.set(rental, value);
			return true;
		} catch (NoSuchFieldException e) {
			throw new PatchException("Object RentalDTO doesn't have field '"
					+ e.getMessage() + "'", e.getMessage());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassCastException | NoSuchElementException e) {
			throw new PatchException(e.getMessage(), rentalAttribute);
		}
		return false;
	}

}
