package com.dcmc.apps.taskmanager.service.dto;


import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class CreatePriorityDTO implements Serializable {

    @NotNull
    private String name;


    private String description;

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
