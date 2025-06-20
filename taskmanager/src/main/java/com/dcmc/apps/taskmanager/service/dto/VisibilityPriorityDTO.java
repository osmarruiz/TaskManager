package com.dcmc.apps.taskmanager.service.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public class VisibilityPriorityDTO implements Serializable {

    @NotNull
    private Boolean visible;

    // Getter y Setter
    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
