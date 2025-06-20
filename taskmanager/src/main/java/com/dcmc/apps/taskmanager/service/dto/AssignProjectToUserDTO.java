package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AssignProjectToUserDTO {

    @NotBlank
    private String userLogin; // Username del usuario a asignar

    // Getters y Setters

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
