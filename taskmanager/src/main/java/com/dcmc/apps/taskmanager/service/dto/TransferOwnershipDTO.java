package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class TransferOwnershipDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private String newOwnerUserId;

    // Getters y Setters
    public String getNewOwnerUserId() {
        return newOwnerUserId;
    }

    public void setNewOwnerUserId(String newOwnerUserId) {
        this.newOwnerUserId = newOwnerUserId;
    }
}
