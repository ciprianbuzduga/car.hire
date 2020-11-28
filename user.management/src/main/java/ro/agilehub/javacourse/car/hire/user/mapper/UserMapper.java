package ro.agilehub.javacourse.car.hire.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ro.agilehub.javacourse.car.hire.api.model.UserRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.UserResponseDTO;
import ro.agilehub.javacourse.car.hire.user.document.UserDoc;

@Mapper
public interface UserMapper {

	@Mapping(target = "status", expression = "java(ro.agilehub.javacourse.car.hire.user.document.UserStatusEnum.ACTIVE)")
	UserDoc mapToUserDoc(UserRequestDTO userDTO);

	@Mapping(target = "id", source = "_id")
	UserResponseDTO mapToUserResponseDTO(UserDoc userDoc);

}
