package com.dcmc.apps.taskmanager.service.dto;

import com.dcmc.apps.taskmanager.domain.enumeration.Role;

import java.io.Serializable;

public class UserWorkGroupDTO implements Serializable {
    private Long workGroupId;
    private String workGroupName;
    private Role userRole;

    // Constructores, getters y setters
    public UserWorkGroupDTO(Long workGroupId, String workGroupName, Role userRole) {
        this.workGroupId = workGroupId;
        this.workGroupName = workGroupName;
        this.userRole = userRole;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }

    public Role getUserRole() {
        return userRole;
    }


}
