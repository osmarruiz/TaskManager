package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;


public class CreateTaskStatusCatalogDTO implements Serializable {

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @Size(max = 500)
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

    @Override
    public String toString() {
        return "CreateTaskStatusCatalogDTO{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
