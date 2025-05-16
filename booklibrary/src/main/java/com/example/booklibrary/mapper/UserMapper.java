package com.example.booklibrary.mapper;

import com.example.booklibrary.dto.request.user.UserCreateDTO;
import com.example.booklibrary.dto.request.user.UserUpdateDTO;
import com.example.booklibrary.dto.response.user.UserDTO;
import com.example.booklibrary.model.Role;
import com.example.booklibrary.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "rentals", ignore = true)
    User toEntity(UserCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "rentals", ignore = true)
    void updateFromDto(UserUpdateDTO dto, @MappingTarget User user);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRolesToStrings")
    UserDTO toDto(User user);

    @Named("mapRolesToStrings")
    default List<String> mapRolesToStrings(List<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}