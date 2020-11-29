package ro.agilehub.javacourse.car.hire.boot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import ro.agilehub.javacourse.car.hire.fleet.converter.CarClazzConverter;
import ro.agilehub.javacourse.car.hire.fleet.converter.CarStatusConverter;
import ro.agilehub.javacourse.car.hire.rental.converter.RentalStatusConverter;
import ro.agilehub.javacourse.car.hire.user.converter.CountryStatusConverter;

import java.util.List;

@Configuration
public class MongoConfiguration {

	@Bean
	public MongoCustomConversions customConversions() {
		return new MongoCustomConversions(List.of(CarClazzConverter.INSTANCE,
				CarStatusConverter.INSTANCE, CountryStatusConverter.INSTANCE,
				RentalStatusConverter.INSTANCE));
	}
}
