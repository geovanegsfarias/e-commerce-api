package com.github.geovanegsfarias.user;

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
