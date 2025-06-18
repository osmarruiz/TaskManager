package com.dcmc.apps.taskmanager.service.dto;


import jakarta.validation.constraints.NotNull;

public class MemberActionDTO {
    @NotNull
    private String userLogin; // Usamos login en lugar de ID para consistencia

    // Getters y setters al estilo JHipster
    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
