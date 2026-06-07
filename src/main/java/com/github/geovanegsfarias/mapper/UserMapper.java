package com.github.geovanegsfarias.mapper;

import com.github.geovanegsfarias.dto.request.CreateUserRequest;
import com.github.geovanegsfarias.dto.response.UserResponse;
import com.github.geovanegsfarias.model.User;

public class UserMapper {

    public static User toUser(CreateUserRequest request) {
        return new User(
                request.username(),
                request.email(),
                request.password()
        );
    }

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

}
