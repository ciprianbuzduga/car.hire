package ro.agilehub.javacourse.car.hire.fleet.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ro.agilehub.javacourse.car.hire.api.exception.DuplicateKeyErrorCollection;
import ro.agilehub.javacourse.car.hire.api.exception.EntityAlreadyExistsException;
import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.fleet.document.MakeCarDoc;
import ro.agilehub.javacourse.car.hire.fleet.mapper.CarMapper;
import ro.agilehub.javacourse.car.hire.fleet.repository.CarRepository;
import ro.agilehub.javacourse.car.hire.fleet.repository.MakeCarRepository;
import ro.agilehub.javacourse.car.hire.fleet.service.impl.CarsServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class CarsServiceTest {

	private static final String MOCK_ID = "1234";

	@InjectMocks
	private CarsServiceImpl carsService;

	@Mock
	private CarRepository carRepository;
	
	@Mock
	private MakeCarRepository makeCarRepository;

	@Mock
	private CarMapper carMapper;

	@Test
	public void test_addCar_whenFindByRegistrationNo_thenException() {
		when(carRepository.countByRegistrationNo(any())).thenReturn(1L);
		
		CarRequestDTO newCar = mock(CarRequestDTO.class);
		
		EntityAlreadyExistsException exception = assertThrows(
				EntityAlreadyExistsException.class,
				() -> carsService.addCar(newCar));
		assertEquals("registrationNo", exception.getField());
	}

	@Test
	public void test_addCar_whenNoMakeFound_thenException() {
		when(carRepository.countByRegistrationNo(any())).thenReturn(0L);
		when(makeCarRepository.findByName(any())).thenReturn(null);
		
		CarRequestDTO newCar = mock(CarRequestDTO.class);
		
		assertThrows(NoSuchElementException.class,
				() -> carsService.addCar(newCar));
	}

	@Test
	public void test_addCar_whenAllOK() {
		MakeCarDoc makeCarDoc = mock(MakeCarDoc.class);
		when(carRepository.countByRegistrationNo(any())).thenReturn(0L);
		when(makeCarRepository.findByName(any())).thenReturn(makeCarDoc);
		
		CarRequestDTO newCar = mock(CarRequestDTO.class);
		CarDoc carDoc = mock(CarDoc.class);
		
		when(carMapper.mapToCarDoc(newCar)).thenReturn(carDoc);
		when(carRepository.save(carDoc)).thenReturn(carDoc);
		when(carDoc.get_id()).thenReturn(MOCK_ID);
		
		String newId = carsService.addCar(newCar);
		assertNotNull(newId);
		assertEquals(carDoc.get_id(), newId);
	}

	@Test
	public void test_addCar_whenConcurrentAccessException() {
		MakeCarDoc makeCarDoc = mock(MakeCarDoc.class);
		when(carRepository.countByRegistrationNo(any())).thenReturn(0L);
		when(makeCarRepository.findByName(any())).thenReturn(makeCarDoc);
		
		CarRequestDTO newCar = mock(CarRequestDTO.class);
		CarDoc carDoc = mock(CarDoc.class);
		DuplicateKeyException duplicateKeyException = mock(DuplicateKeyException.class);
		RuntimeException causeException = mock(RuntimeException.class);
		
		when(carMapper.mapToCarDoc(newCar)).thenReturn(carDoc);
		when(duplicateKeyException.getCause()).thenReturn(causeException);
		when(causeException.getMessage()).thenReturn(any());
		when(carRepository.save(carDoc)).thenThrow(duplicateKeyException);
		
		assertThrows(DuplicateKeyErrorCollection.class,
				() -> carsService.addCar(newCar));
	}
	
	@Test
	public void test_addCar_whenUnknownException() {
		MakeCarDoc makeCarDoc = mock(MakeCarDoc.class);
		when(carRepository.countByRegistrationNo(any())).thenReturn(0L);
		when(makeCarRepository.findByName(any())).thenReturn(makeCarDoc);
		
		CarRequestDTO newCar = mock(CarRequestDTO.class);
		CarDoc carDoc = mock(CarDoc.class);
		RuntimeException unknownException = mock(RuntimeException.class);
		
		when(carMapper.mapToCarDoc(newCar)).thenReturn(carDoc);
		when(carRepository.save(carDoc)).thenThrow(unknownException);
		
		String newId = carsService.addCar(newCar);
		assertNull(newId);
	}

	@Test
	public void test_deleteCarById_whenNotFound() {
		when(carRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> carsService.deleteCar(MOCK_ID));
	}

	@Test
	public void test_deleteCarById_whenFound() {
		CarDoc carDoc = mock(CarDoc.class);
		when(carRepository.findById(MOCK_ID)).thenReturn(Optional.of(carDoc));
		doNothing().when(carRepository).delete(carDoc);
		
		assertTrue(carsService.deleteCar(MOCK_ID));
	}
	
	@Test
	public void test_deleteCarById_whenUnknownException() {
		CarDoc carDoc = mock(CarDoc.class);
		when(carRepository.findById(MOCK_ID)).thenReturn(Optional.of(carDoc));
		RuntimeException unknownException = mock(RuntimeException.class);
		doThrow(unknownException).when(carRepository).delete(carDoc);
		
		assertFalse(carsService.deleteCar(MOCK_ID));
	}
	
	@Test
	public void test_getCarDoc_whenFound() {
		CarDoc carDoc = mock(CarDoc.class);
		when(carRepository.findById(MOCK_ID)).thenReturn(Optional.of(carDoc));
		
		CarDoc carFound = carsService.getCarDoc(MOCK_ID);
		assertNotNull(carFound);
		assertEquals(carDoc, carFound);
	}
	
	@Test
	public void test_getCarDoc_whenNotFound() {
		when(carRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> carsService.getCarDoc(MOCK_ID));
	}
	
	@Test
	public void test_getCar_whenFound() {
		CarDoc carDoc = mock(CarDoc.class);
		when(carRepository.findById(MOCK_ID)).thenReturn(Optional.of(carDoc));
		
		CarResponseDTO mockCarResponse = mock(CarResponseDTO.class);
		when(carMapper.mapToCarResponseDTO(carDoc)).thenReturn(mockCarResponse);
		
		CarResponseDTO carResponse = carsService.getCar(MOCK_ID);
		assertNotNull(carResponse);
		assertEquals(mockCarResponse, carResponse);
	}
	
	@Test
	public void test_getCar_whenNotFound() {
		when(carRepository.findById(MOCK_ID)).thenReturn(Optional.empty());
		
		assertThrows(NoSuchElementException.class,
				() -> carsService.getCar(MOCK_ID));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_findAll_whenFound_firstPage() {
		Page<CarDoc> mockPageCars = mock(Page.class);
		int page = 0;
		int size = 10;
		long totalCarsSize = 50;
		int totalPages = (int) totalCarsSize / size;
		List<CarDoc> listCars = new ArrayList<>(size);
		for(int i = 0; i < size; i++)
			listCars.add(mock(CarDoc.class));
		
		when(mockPageCars.iterator()).thenReturn(listCars.iterator());
		when(mockPageCars.getNumberOfElements()).thenReturn(size);
		when(mockPageCars.getTotalElements()).thenReturn(totalCarsSize);
		when(mockPageCars.getTotalPages()).thenReturn(totalPages);
		when(mockPageCars.hasContent()).thenReturn(true);
		
		when(carRepository.findAllByStatus(anyString(), any(Pageable.class)))
			.thenReturn(mockPageCars);
		when(carMapper.mapToCarResponseDTO(any(CarDoc.class)))
			.thenReturn(mock(CarResponseDTO.class));
		
		doCallRealMethod().when(mockPageCars).forEach(any(Consumer.class));
		
		String mockStatus = "active";
		PageCars pageCars = carsService.findAll(page, size, null, mockStatus);
		assertNotNull(pageCars);
		assertEquals(size, pageCars.getCars().size());
		
		assertEquals(page, pageCars.getCurrentPage());
		assertEquals(size, pageCars.getPageSize());
		assertEquals((int) totalCarsSize, pageCars.getTotalNoRecords());
		assertEquals(totalPages, pageCars.getTotalPages());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_findAll_whenNotFound() {
		Page<CarDoc> mockPageCars = mock(Page.class);
		when(mockPageCars.getNumberOfElements()).thenReturn(0);
		when(mockPageCars.getTotalElements()).thenReturn(0l);
		when(mockPageCars.getTotalPages()).thenReturn(0);
		when(mockPageCars.hasContent()).thenReturn(false);
		
		when(carRepository.findAllByStatus(anyString(), any(Pageable.class)))
			.thenReturn(mockPageCars);
		
		String mockStatus = "active";
		PageCars pageCars = carsService.findAll(0, 10, null, mockStatus);
		assertNotNull(pageCars);
		assertNull(pageCars.getCars());
		assertEquals(0, pageCars.getCurrentPage());
		assertEquals(0, pageCars.getPageSize());
		assertEquals(0, pageCars.getTotalNoRecords());
		assertEquals(0, pageCars.getTotalPages());
	}
	
	//TODO Patch car Test
}
