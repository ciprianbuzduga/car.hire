package ro.agilehub.javacourse.car.hire.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ro.agilehub.javacourse.car.hire.api.model.CountryRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CountryResponseDTO;
import ro.agilehub.javacourse.car.hire.user.document.CountryDoc;

@Mapper(componentModel = "spring")
public interface CountryMapper {

	@Mapping(target = "status", expression = "java(ro.agilehub.javacourse.car.hire.user.document.CountryStatusEnum.ACTIVE)")
	CountryDoc mapToCountryDoc(CountryRequestDTO countryDTO);

	@Mapping(target = "id", source = "_id")
	CountryResponseDTO mapToCountryResponseDTO(CountryDoc countryDoc);
}
