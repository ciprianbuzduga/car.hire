package ro.agilehub.javacourse.car.hire.fleet.controller;

import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarStatusDTO;
import ro.agilehub.javacourse.car.hire.base.test.MockMvcSetup;
import ro.agilehub.javacourse.car.hire.fleet.service.CarsService;

@RunWith(SpringRunner.class)
@WebMvcTest(CarsController.class)
public class UnauthorizedCarsControllerTest extends MockMvcSetup {
	private static final String ID = "1234";

	@MockBean
	private CarsService carsService;

	@Test
	public void test_addCar() throws Exception {
		CarRequestDTO newCar = mock(CarRequestDTO.class);
		
		mvc.perform(post(PATH_CARS)
				.contentType(APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newCar)))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void test_deleteCar() throws Exception {
		mvc.perform(delete(PATH_CARS + "/" + ID))
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void test_getCar() throws Exception {
		mvc.perform(get(PATH_CARS + "/" + ID))
			.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void test_getCars() throws Exception {
		mvc.perform(get(PATH_CARS)
				.queryParam("status", CarStatusDTO.ACTIVE.name()))
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void test_updateCar() throws Exception {
		mvc.perform(patch(PATH_CARS + "/" + ID))
			.andExpect(status().isUnauthorized());
	}
}
