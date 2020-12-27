package ro.agilehub.javacourse.car.hire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "ro.agilehub.javacourse.car.hire")
@SpringBootApplication
public class TestFleetConfiguration {

	public static void main(final String[] args) {
		SpringApplication.run(TestFleetConfiguration.class, args);
	}
}
