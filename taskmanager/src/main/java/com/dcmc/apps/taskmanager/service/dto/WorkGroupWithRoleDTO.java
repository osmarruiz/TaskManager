package com.dcmc.apps.taskmanager.service.dto;

import java.io.Serializable;

public class WorkGroupWithRoleDTO implements Serializable {
    private WorkGroupDTO workGroup;
    private String role;

    public WorkGroupWithRoleDTO() {}

    public WorkGroupWithRoleDTO(WorkGroupDTO workGroup, String role) {
        this.workGroup = workGroup;
        this.role = role;
    }

    public WorkGroupDTO getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(WorkGroupDTO workGroup) {
        this.workGroup = workGroup;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
