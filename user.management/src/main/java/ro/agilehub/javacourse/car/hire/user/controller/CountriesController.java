package ro.agilehub.javacourse.car.hire.user.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import ro.agilehub.javacourse.car.hire.api.model.CountryRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CountryResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.api.specification.CountriesApi;
import ro.agilehub.javacourse.car.hire.user.service.CountriesService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class CountriesController implements CountriesApi {

	private final CountriesService countriesService;

	@Override
	public ResponseEntity<Void> createCountry(@Valid CountryRequestDTO countryDTO) {
		String newId = countriesService.createCountry(countryDTO);
		if(newId != null) {
			UriComponents uriComponents = UriComponentsBuilder.newInstance()
					.scheme("http").host("localhost").port(8080)
					.path("/countries/{id}").buildAndExpand(newId);
			return ResponseEntity.created(uriComponents.toUri()).build();
		} else
			throw new ServerErrorException("Cannot create the country because of "
					+ "unknown reasone", (Throwable)null);
	}

	@Override
	public ResponseEntity<Void> deleteCountry(String id) {
		boolean removed = countriesService.removeCountryById(id);
		if(removed) {
			return ResponseEntity.noContent().build();
		} else 
			throw new ServerErrorException("Cannot remove the country " + id
					+ " because of unknown reasone", (Throwable)null);
	}

	@Override
	public ResponseEntity<CountryResponseDTO> getCountry(String id) {
		CountryResponseDTO country = countriesService.getCountry(id);
		return ResponseEntity.ok(country);
	}

	@Override
	public ResponseEntity<List<CountryResponseDTO>> getCountries() {
		List<CountryResponseDTO> countries = countriesService.findAll();
		return ResponseEntity.ok(countries);
	}

	@Override
	public ResponseEntity<Void> updateCountry(String id,
			@Valid List<PatchDocument> patchDocument) {
		boolean update = countriesService.updateCountry(id, patchDocument);
		if(update)
			return ResponseEntity.noContent().build();
		else
			throw new ServerErrorException("Cannot update the country " + id
					+ " because of unknown reasone", (Throwable)null);
	}
}
