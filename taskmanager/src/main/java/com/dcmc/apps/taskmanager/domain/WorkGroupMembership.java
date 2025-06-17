package com.dcmc.apps.taskmanager.domain;

import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * A WorkGroupMembership.
 */
@Entity
@Table(name = "work_group_membership")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkGroupMembership implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @NotNull
    @Column(name = "join_date", nullable = false)
    private Instant joinDate = Instant.now();

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    @ManyToOne(optional = false)
    @NotNull
    private WorkGroup workGroup;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public WorkGroupMembership id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return this.role;
    }

    public WorkGroupMembership role(Role role) {
        this.setRole(role);
        return this;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Instant getJoinDate() {
        return this.joinDate;
    }

    public WorkGroupMembership joinDate(Instant joinDate) {
        this.setJoinDate(joinDate);
        return this;
    }

    public void setJoinDate(Instant joinDate) {
        this.joinDate = joinDate;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public WorkGroupMembership user(User user) {
        this.setUser(user);
        return this;
    }

    public WorkGroup getWorkGroup() {
        return this.workGroup;
    }

    public void setWorkGroup(WorkGroup workGroup) {
        this.workGroup = workGroup;
    }

    public WorkGroupMembership workGroup(WorkGroup workGroup) {
        this.setWorkGroup(workGroup);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkGroupMembership)) {
            return false;
        }
        return getId() != null && getId().equals(((WorkGroupMembership) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkGroupMembership{" +
            "id=" + getId() +
            ", role='" + getRole() + "'" +
            ", joinDate='" + getJoinDate() + "'" +
            "}";
    }
}
