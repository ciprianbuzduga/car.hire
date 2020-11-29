package ro.agilehub.javacourse.car.hire.rental.mapper;

import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface RentalDateMapper {

	default LocalDateTime toLocalDateTime(OffsetDateTime date) {
		if(date == null)
			return null;
		return date.toLocalDateTime();
	}

	default OffsetDateTime toOffsetDateTime(LocalDateTime date) {
		if(date == null)
			return null;
		return OffsetDateTime.of(date, ZoneOffset.UTC);
	}
}
