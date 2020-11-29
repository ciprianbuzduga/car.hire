package ro.agilehub.javacourse.car.hire.fleet.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;

@Mapper(unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE,
	componentModel = "spring")
@DecoratedWith(CarMapperDecorator.class)
public interface CarMapper {

	@Mapping(target = "status",
			expression = "java(ro.agilehub.javacourse.car.hire.fleet.document.CarStatusEnum.ACTIVE)")
	CarDoc mapToCarDoc(CarRequestDTO carDTO);

	@Mapping(target = "id", source = "_id")
	@Mapping(target = "make",
			expression = "java(carDoc.getMakeCar() != null ? carDoc.getMakeCar().getName() : null)")
	CarResponseDTO mapToCarResponseDTO(CarDoc carDoc);
}
