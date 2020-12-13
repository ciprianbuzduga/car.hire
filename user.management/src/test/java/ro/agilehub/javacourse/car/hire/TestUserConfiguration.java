package ro.agilehub.javacourse.car.hire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "ro.agilehub.javacourse.car.hire")
@SpringBootApplication
public class TestUserConfiguration {

	public static void main(final String[] args) {
		SpringApplication.run(TestUserConfiguration.class, args);
	}
}
