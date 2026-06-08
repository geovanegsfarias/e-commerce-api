package com.github.geovanegsfarias.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "username")
    @Mapping(target = "role", constant = "ROLE_USER")
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toUser(CreateUserRequest request);

    @Mapping(target = "username", source = "name")
    UserResponse toUserResponse(User user);
}