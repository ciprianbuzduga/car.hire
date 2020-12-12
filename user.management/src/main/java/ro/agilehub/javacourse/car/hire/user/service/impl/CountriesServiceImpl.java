package ro.agilehub.javacourse.car.hire.user.service.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ro.agilehub.javacourse.car.hire.api.common.PatchMapper;
import ro.agilehub.javacourse.car.hire.api.model.CountryRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CountryResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.user.document.CountryDoc;
import ro.agilehub.javacourse.car.hire.user.mapper.CountryMapper;
import ro.agilehub.javacourse.car.hire.user.repository.CountryRepository;
import ro.agilehub.javacourse.car.hire.user.service.CountriesService;

@Service
@RequiredArgsConstructor
public class CountriesServiceImpl implements CountriesService {

	private final CountryRepository repository;
	private final CountryMapper countryMapper;

	@Override
	public String createCountry(CountryRequestDTO country) {
		try {
			CountryDoc doc = countryMapper.mapToCountryDoc(country);
			CountryDoc savedDoc = repository.save(doc);
			return savedDoc.get_id();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean removeCountryById(String id) {
		CountryDoc country = getCountryDoc(id);
		try {
			repository.delete(country);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public CountryResponseDTO getCountry(String id) {
		CountryDoc doc = getCountryDoc(id);
		return countryMapper.mapToCountryResponseDTO(doc);
	}

	private CountryDoc getCountryDoc(String id) {
		CountryDoc doc = repository.findById(id).orElseThrow(
				() -> new NoSuchElementException("No country found with id " + id));
		return doc;
	}

	@Override
	public List<CountryResponseDTO> findAll() {
		return repository.findAll().stream().map(
					c -> countryMapper.mapToCountryResponseDTO(c))
				.collect(Collectors.toList());
	}

	@Override
	public boolean updateCountry(String id, List<PatchDocument> patchDocuments) {
		PatchMapper patchMapper = PatchMapper.getPatchMapper(patchDocuments, CountryDoc.class);
		return repository.updateDoc(CountryDoc.class, id, patchMapper.getFieldValues());
	}

}
