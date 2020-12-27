package ro.agilehub.javacourse.car.hire.user.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;

import ro.agilehub.javacourse.car.hire.api.exception.DuplicateKeyErrorCollection;
import ro.agilehub.javacourse.car.hire.api.exception.EntityAlreadyExistsException;
import ro.agilehub.javacourse.car.hire.api.model.CountryRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CountryResponseDTO;
import ro.agilehub.javacourse.car.hire.user.document.CountryDoc;
import ro.agilehub.javacourse.car.hire.user.mapper.CountryMapper;
import ro.agilehub.javacourse.car.hire.user.repository.CountryRepository;
import ro.agilehub.javacourse.car.hire.user.service.impl.CountriesServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class CountriesServiceTest {

	private static final String MOCK_ID = "1234";

	@InjectMocks
	private CountriesServiceImpl countriesService;

	@Mock
	private CountryRepository countryRepository;

	@Mock
	private CountryMapper countryMapper;

	@Test
	public void test_createCountry_whenFindByIsoCode_thenException() {
		when(countryRepository.countByIsoCodeIgnoreCase(any())).thenReturn(1L);
		
		CountryRequestDTO newCountry = mock(CountryRequestDTO.class);
		
		EntityAlreadyExistsException exception = assertThrows(
				EntityAlreadyExistsException.class,
				() -> countriesService.createCountry(newCountry));
		assertEquals("isoCode", exception.getField());
	}

	@Test
	public void test_createCountry_whenFindByName_thenException() {
		when(countryRepository.countByIsoCodeIgnoreCase(any())).thenReturn(0L);
		when(countryRepository.countByNameIgnoreCase(any())).thenReturn(1L);
		
		CountryRequestDTO newCountry = mock(CountryRequestDTO.class);
		
		EntityAlreadyExistsException exception = assertThrows(
				EntityAlreadyExistsException.class,
				() -> countriesService.createCountry(newCountry));
		assertEquals("name", exception.getField());
	}

	@Test
	public void test_createCountry_whenAllOK() {
		when(countryRepository.countByIsoCodeIgnoreCase(any())).thenReturn(0L);
		when(countryRepository.countByNameIgnoreCase(any())).thenReturn(0L);
		
		CountryRequestDTO newCountry = mock(CountryRequestDTO.class);
		CountryDoc countryDoc = mock(CountryDoc.class);
		
		when(countryMapper.mapToCountryDoc(newCountry)).thenReturn(countryDoc);
		when(countryRepository.save(countryDoc)).thenReturn(countryDoc);
		when(countryDoc.get_id()).thenReturn(MOCK_ID);
		
		String newId = countriesService.createCountry(newCountry);
		assertNotNull(newId);
		assertEquals(countryDoc.get_id(), newId);
	}

	@Test
	public void test_createCountry_whenConcurrentAccessException() {
		when(countryRepository.countByIsoCodeIgnoreCase(any())).thenReturn(0L);
		when(countryRepository.countByNameIgnoreCase(any())).thenReturn(0L);
		
		CountryRequestDTO newCountry = mock(CountryRequestDTO.class);
		CountryDoc countryDoc = mock(CountryDoc.class);
		DuplicateKeyException duplicateKeyException = mock(DuplicateKeyException.class);
		RuntimeException causeException = mock(RuntimeException.class);
		
		when(countryMapper.mapToCountryDoc(newCountry)).thenReturn(countryDoc);
		when(duplicateKeyException.getCause()).thenReturn(causeException);
		when(causeException.getMessage()).thenReturn(any());
		when(countryRepository.save(countryDoc)).thenThrow(duplicateKeyException);
		
		assertThrows(DuplicateKeyErrorCollection.class,
				() -> countriesService.createCountry(newCountry));
	}
	
	@Test
	public void test_createCountry_whenUnknownException() {
		when(countryRepository.countByIsoCodeIgnoreCase(any())).thenReturn(0L);
		when(countryRepository.countByNameIgnoreCase(any())).thenReturn(0L);
		
		CountryRequestDTO newCountry = mock(CountryRequestDTO.class);
		CountryDoc countryDoc = mock(CountryDoc.class);
		RuntimeException unknownException = mock(RuntimeException.class);
		
		when(countryMapper.mapToCountryDoc(newCountry)).thenReturn(countryDoc);
		when(countryRepository.save(countryDoc)).thenThrow(unknownException);
		
		String newId = countriesService.createCountry(newCountry);
		assertNull(newId);
	}

	@Test
	public void test_removeCountryById_whenNotFound() {
		when(countryRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> countriesService.removeCountryById(MOCK_ID));
	}

	@Test
	public void test_removeCountryById_whenFound() {
		CountryDoc countryDoc = mock(CountryDoc.class);
		when(countryRepository.findById(MOCK_ID)).thenReturn(Optional.of(countryDoc));
		doNothing().when(countryRepository).delete(countryDoc);
		
		assertTrue(countriesService.removeCountryById(MOCK_ID));
	}
	
	@Test
	public void test_removeCountryById_whenUnknownException() {
		CountryDoc countryDoc = mock(CountryDoc.class);
		when(countryRepository.findById(MOCK_ID)).thenReturn(Optional.of(countryDoc));
		RuntimeException unknownException = mock(RuntimeException.class);
		doThrow(unknownException).when(countryRepository).delete(countryDoc);
		
		assertFalse(countriesService.removeCountryById(MOCK_ID));
	}
	
	@Test
	public void test_getCountryDoc_whenFound() {
		CountryDoc countryDoc = mock(CountryDoc.class);
		when(countryRepository.findById(MOCK_ID)).thenReturn(Optional.of(countryDoc));
		
		CountryDoc countryFound = countriesService.getCountryDoc(MOCK_ID);
		assertNotNull(countryFound);
		assertEquals(countryDoc, countryFound);
	}
	
	@Test
	public void test_getCountryDoc_whenNotFound() {
		when(countryRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> countriesService.getCountryDoc(MOCK_ID));
	}
	
	@Test
	public void test_getCountry_whenFound() {
		CountryDoc countryMock = mock(CountryDoc.class);
		when(countryRepository.findById(MOCK_ID)).thenReturn(Optional.of(countryMock));
		
		CountryResponseDTO mockCountryResponse = mock(CountryResponseDTO.class);
		when(countryMapper.mapToCountryResponseDTO(countryMock)).thenReturn(mockCountryResponse);
		
		CountryResponseDTO countryResponse = countriesService.getCountry(MOCK_ID);
		assertNotNull(countryResponse);
		assertEquals(mockCountryResponse, countryResponse);
	}
	
	@Test
	public void test_getCountry_whenNotFound() {
		when(countryRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> countriesService.getCountry(MOCK_ID));
	}
	
	@Test
	public void test_findAll_whenFound() {
		List<CountryDoc> mockList = new ArrayList<>(5);
		for(int i = 0; i < 5; i++)
			mockList.add(mock(CountryDoc.class));
		when(countryRepository.findAll()).thenReturn(mockList);
		when(countryMapper.mapToCountryResponseDTO(any()))
			.thenReturn(mock(CountryResponseDTO.class));
		
		List<CountryResponseDTO> resultList = countriesService.findAll();
		assertEquals(5, resultList.size());
	}
	
	@Test
	public void test_findAll_whenNotFound() {
		when(countryRepository.findAll()).thenReturn(null);
		
		List<CountryResponseDTO> resultList = countriesService.findAll();
		assertTrue(resultList.isEmpty());
	}
	
	//TODO Patch user Test
}
