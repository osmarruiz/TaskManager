package com.dcmc.apps.taskmanager.service.criteria;

import com.dcmc.apps.taskmanager.domain.enumeration.Role;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dcmc.apps.taskmanager.domain.WorkGroupMembership} entity. This class is used
 * in {@link com.dcmc.apps.taskmanager.web.rest.WorkGroupMembershipResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /work-group-memberships?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class WorkGroupMembershipCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Role
     */
    public static class RoleFilter extends Filter<Role> {

        public RoleFilter() {}

        public RoleFilter(RoleFilter filter) {
            super(filter);
        }

        @Override
        public RoleFilter copy() {
            return new RoleFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private RoleFilter role;

    private InstantFilter joinDate;

    private StringFilter userId;

    private LongFilter workGroupId;

    private Boolean distinct;

    public WorkGroupMembershipCriteria() {}

    public WorkGroupMembershipCriteria(WorkGroupMembershipCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.role = other.optionalRole().map(RoleFilter::copy).orElse(null);
        this.joinDate = other.optionalJoinDate().map(InstantFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.workGroupId = other.optionalWorkGroupId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public WorkGroupMembershipCriteria copy() {
        return new WorkGroupMembershipCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public RoleFilter getRole() {
        return role;
    }

    public Optional<RoleFilter> optionalRole() {
        return Optional.ofNullable(role);
    }

    public RoleFilter role() {
        if (role == null) {
            setRole(new RoleFilter());
        }
        return role;
    }

    public void setRole(RoleFilter role) {
        this.role = role;
    }

    public InstantFilter getJoinDate() {
        return joinDate;
    }

    public Optional<InstantFilter> optionalJoinDate() {
        return Optional.ofNullable(joinDate);
    }

    public InstantFilter joinDate() {
        if (joinDate == null) {
            setJoinDate(new InstantFilter());
        }
        return joinDate;
    }

    public void setJoinDate(InstantFilter joinDate) {
        this.joinDate = joinDate;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public Optional<StringFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public StringFilter userId() {
        if (userId == null) {
            setUserId(new StringFilter());
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    public LongFilter getWorkGroupId() {
        return workGroupId;
    }

    public Optional<LongFilter> optionalWorkGroupId() {
        return Optional.ofNullable(workGroupId);
    }

    public LongFilter workGroupId() {
        if (workGroupId == null) {
            setWorkGroupId(new LongFilter());
        }
        return workGroupId;
    }

    public void setWorkGroupId(LongFilter workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final WorkGroupMembershipCriteria that = (WorkGroupMembershipCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(role, that.role) &&
            Objects.equals(joinDate, that.joinDate) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(workGroupId, that.workGroupId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, joinDate, userId, workGroupId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "WorkGroupMembershipCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalRole().map(f -> "role=" + f + ", ").orElse("") +
            optionalJoinDate().map(f -> "joinDate=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalWorkGroupId().map(f -> "workGroupId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
