package ro.agilehub.javacourse.car.hire.user.service;

import java.util.List;

import ro.agilehub.javacourse.car.hire.api.model.CountryRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CountryResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;

public interface CountriesService {

	String createCountry(CountryRequestDTO country);

	boolean removeCountryById(String id);

	CountryResponseDTO getCountry(String id);

	List<CountryResponseDTO> findAll();

	boolean updateCountry(String id, List<PatchDocument> patchDocuments);

}
