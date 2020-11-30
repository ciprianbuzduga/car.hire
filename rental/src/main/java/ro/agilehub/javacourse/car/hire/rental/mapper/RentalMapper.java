package ro.agilehub.javacourse.car.hire.rental.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ro.agilehub.javacourse.car.hire.api.model.RentalRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.RentalResponseDTO;
import ro.agilehub.javacourse.car.hire.fleet.mapper.CarMapper;
import ro.agilehub.javacourse.car.hire.rental.document.RentalDoc;
import ro.agilehub.javacourse.car.hire.user.mapper.UserMapper;

@Mapper(uses = {RentalDateMapper.class, UserMapper.class, CarMapper.class},
		componentModel = "spring")
public interface RentalMapper {

	@Mapping(target = "status",
			expression = "java(ro.agilehub.javacourse.car.hire.rental.document.RentalStatusEnum.ACTIVE)")
	RentalDoc mapToRentalDoc(RentalRequestDTO rentalDTO);

	@Mapping(target = "id", source = "_id")
	RentalResponseDTO mapToRentalResponseDTO(RentalDoc rentalDoc);
}
