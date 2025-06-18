package com.dcmc.apps.taskmanager.service.dto;


import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class ModeratorActionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private String userId;

    // Getters y setters al estilo JHipster
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
