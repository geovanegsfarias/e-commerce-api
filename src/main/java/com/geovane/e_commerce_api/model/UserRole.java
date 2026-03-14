package com.geovane.e_commerce_api.model;

public enum UserRole {
    ROLE_USER("user"),
    ROLE_ADMIN("admin");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}
