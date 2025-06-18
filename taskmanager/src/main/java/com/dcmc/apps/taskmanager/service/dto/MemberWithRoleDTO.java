package com.dcmc.apps.taskmanager.service.dto;

import com.dcmc.apps.taskmanager.domain.enumeration.Role;

import java.io.Serializable;
import java.time.Instant;

public class MemberWithRoleDTO implements Serializable {
    private String userLogin;
    private String userName;
    private Role role;
    private Instant joinDate;

    public MemberWithRoleDTO(String userLogin, String userName, Role role, Instant joinDate) {
        this.userLogin = userLogin;
        this.userName = userName;
        this.role = role;
        this.joinDate = joinDate;
    }

    public String getUserLogin() {
        return userLogin;
    }
    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public Instant getJoinDate() {
        return joinDate;
    }
    public void setJoinDate(Instant joinDate) {
        this.joinDate = joinDate;
    }

}
