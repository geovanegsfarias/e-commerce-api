package com.geovane.e_commerce_api.mapper;

import com.geovane.e_commerce_api.dto.request.CreateUserRequest;
import com.geovane.e_commerce_api.dto.response.UserResponse;
import com.geovane.e_commerce_api.model.User;

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
