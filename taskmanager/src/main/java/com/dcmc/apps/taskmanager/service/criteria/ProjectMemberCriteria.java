package com.dcmc.apps.taskmanager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dcmc.apps.taskmanager.domain.ProjectMember} entity. This class is used
 * in {@link com.dcmc.apps.taskmanager.web.rest.ProjectMemberResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /project-members?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectMemberCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter assignedAt;

    private LongFilter projectId;

    private StringFilter userId;

    private Boolean distinct;

    public ProjectMemberCriteria() {}

    public ProjectMemberCriteria(ProjectMemberCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.assignedAt = other.optionalAssignedAt().map(InstantFilter::copy).orElse(null);
        this.projectId = other.optionalProjectId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProjectMemberCriteria copy() {
        return new ProjectMemberCriteria(this);
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

    public InstantFilter getAssignedAt() {
        return assignedAt;
    }

    public Optional<InstantFilter> optionalAssignedAt() {
        return Optional.ofNullable(assignedAt);
    }

    public InstantFilter assignedAt() {
        if (assignedAt == null) {
            setAssignedAt(new InstantFilter());
        }
        return assignedAt;
    }

    public void setAssignedAt(InstantFilter assignedAt) {
        this.assignedAt = assignedAt;
    }

    public LongFilter getProjectId() {
        return projectId;
    }

    public Optional<LongFilter> optionalProjectId() {
        return Optional.ofNullable(projectId);
    }

    public LongFilter projectId() {
        if (projectId == null) {
            setProjectId(new LongFilter());
        }
        return projectId;
    }

    public void setProjectId(LongFilter projectId) {
        this.projectId = projectId;
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
        final ProjectMemberCriteria that = (ProjectMemberCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(assignedAt, that.assignedAt) &&
            Objects.equals(projectId, that.projectId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, assignedAt, projectId, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectMemberCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalAssignedAt().map(f -> "assignedAt=" + f + ", ").orElse("") +
            optionalProjectId().map(f -> "projectId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
