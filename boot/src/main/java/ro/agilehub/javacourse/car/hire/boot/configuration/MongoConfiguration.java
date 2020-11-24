package ro.agilehub.javacourse.car.hire.boot.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import ro.agilehub.javacourse.car.hire.converter.CarClazzConverter;
import ro.agilehub.javacourse.car.hire.converter.CarStatusConverter;
import ro.agilehub.javacourse.car.hire.converter.CountryStatusConverter;
import ro.agilehub.javacourse.car.hire.converter.RentalStatusConverter;

@Configuration
public class MongoConfiguration {

	@Bean
	public MongoCustomConversions customConversions() {
		return new MongoCustomConversions(List.of(CarClazzConverter.INSTANCE,
				CarStatusConverter.INSTANCE, CountryStatusConverter.INSTANCE,
				RentalStatusConverter.INSTANCE));
	}
}
