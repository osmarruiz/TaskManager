package com.dcmc.apps.taskmanager.service.dto;

import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.dcmc.apps.taskmanager.domain.WorkGroupMembership} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CreateWorkGroupMembershipDTO implements Serializable {

    @NotNull
    private Role role;

    @NotNull
    private Long workGroupId;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }



    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

}
