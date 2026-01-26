package dev.weewee.userservice.domain.db;

import dev.weewee.api.http.user.AddressDto;
import dev.weewee.api.http.user.CreateAddressRequestDto;
import dev.weewee.api.http.user.CreateUserRequestDto;
import dev.weewee.api.http.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserEntity toEntity(CreateUserRequestDto dto);

    UserDto toDto(UserEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    AddressEntity toEntity(CreateAddressRequestDto dto);

    AddressDto toDto(AddressEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget UserEntity entity, dev.weewee.api.http.user.UpdateUserRequestDto dto);
}

