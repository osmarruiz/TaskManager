package com.dcmc.apps.taskmanager.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.dcmc.apps.taskmanager.domain.Task} entity. This class is used
 * in {@link com.dcmc.apps.taskmanager.web.rest.TaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TaskCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private InstantFilter createTime;

    private InstantFilter updateTime;

    private ZonedDateTimeFilter deadline;

    private BooleanFilter archived;

    private ZonedDateTimeFilter archivedDate;

    private LongFilter workGroupId;

    private LongFilter priorityId;

    private LongFilter statusId;

    private LongFilter parentProjectId;

    private Boolean distinct;

    public TaskCriteria() {}

    public TaskCriteria(TaskCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.createTime = other.optionalCreateTime().map(InstantFilter::copy).orElse(null);
        this.updateTime = other.optionalUpdateTime().map(InstantFilter::copy).orElse(null);
        this.deadline = other.optionalDeadline().map(ZonedDateTimeFilter::copy).orElse(null);
        this.archived = other.optionalArchived().map(BooleanFilter::copy).orElse(null);
        this.archivedDate = other.optionalArchivedDate().map(ZonedDateTimeFilter::copy).orElse(null);
        this.workGroupId = other.optionalWorkGroupId().map(LongFilter::copy).orElse(null);
        this.priorityId = other.optionalPriorityId().map(LongFilter::copy).orElse(null);
        this.statusId = other.optionalStatusId().map(LongFilter::copy).orElse(null);
        this.parentProjectId = other.optionalParentProjectId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TaskCriteria copy() {
        return new TaskCriteria(this);
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

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public InstantFilter getCreateTime() {
        return createTime;
    }

    public Optional<InstantFilter> optionalCreateTime() {
        return Optional.ofNullable(createTime);
    }

    public InstantFilter createTime() {
        if (createTime == null) {
            setCreateTime(new InstantFilter());
        }
        return createTime;
    }

    public void setCreateTime(InstantFilter createTime) {
        this.createTime = createTime;
    }

    public InstantFilter getUpdateTime() {
        return updateTime;
    }

    public Optional<InstantFilter> optionalUpdateTime() {
        return Optional.ofNullable(updateTime);
    }

    public InstantFilter updateTime() {
        if (updateTime == null) {
            setUpdateTime(new InstantFilter());
        }
        return updateTime;
    }

    public void setUpdateTime(InstantFilter updateTime) {
        this.updateTime = updateTime;
    }

    public ZonedDateTimeFilter getDeadline() {
        return deadline;
    }

    public Optional<ZonedDateTimeFilter> optionalDeadline() {
        return Optional.ofNullable(deadline);
    }

    public ZonedDateTimeFilter deadline() {
        if (deadline == null) {
            setDeadline(new ZonedDateTimeFilter());
        }
        return deadline;
    }

    public void setDeadline(ZonedDateTimeFilter deadline) {
        this.deadline = deadline;
    }

    public BooleanFilter getArchived() {
        return archived;
    }

    public Optional<BooleanFilter> optionalArchived() {
        return Optional.ofNullable(archived);
    }

    public BooleanFilter archived() {
        if (archived == null) {
            setArchived(new BooleanFilter());
        }
        return archived;
    }

    public void setArchived(BooleanFilter archived) {
        this.archived = archived;
    }

    public ZonedDateTimeFilter getArchivedDate() {
        return archivedDate;
    }

    public Optional<ZonedDateTimeFilter> optionalArchivedDate() {
        return Optional.ofNullable(archivedDate);
    }

    public ZonedDateTimeFilter archivedDate() {
        if (archivedDate == null) {
            setArchivedDate(new ZonedDateTimeFilter());
        }
        return archivedDate;
    }

    public void setArchivedDate(ZonedDateTimeFilter archivedDate) {
        this.archivedDate = archivedDate;
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

    public LongFilter getPriorityId() {
        return priorityId;
    }

    public Optional<LongFilter> optionalPriorityId() {
        return Optional.ofNullable(priorityId);
    }

    public LongFilter priorityId() {
        if (priorityId == null) {
            setPriorityId(new LongFilter());
        }
        return priorityId;
    }

    public void setPriorityId(LongFilter priorityId) {
        this.priorityId = priorityId;
    }

    public LongFilter getStatusId() {
        return statusId;
    }

    public Optional<LongFilter> optionalStatusId() {
        return Optional.ofNullable(statusId);
    }

    public LongFilter statusId() {
        if (statusId == null) {
            setStatusId(new LongFilter());
        }
        return statusId;
    }

    public void setStatusId(LongFilter statusId) {
        this.statusId = statusId;
    }

    public LongFilter getParentProjectId() {
        return parentProjectId;
    }

    public Optional<LongFilter> optionalParentProjectId() {
        return Optional.ofNullable(parentProjectId);
    }

    public LongFilter parentProjectId() {
        if (parentProjectId == null) {
            setParentProjectId(new LongFilter());
        }
        return parentProjectId;
    }

    public void setParentProjectId(LongFilter parentProjectId) {
        this.parentProjectId = parentProjectId;
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
        final TaskCriteria that = (TaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(createTime, that.createTime) &&
            Objects.equals(updateTime, that.updateTime) &&
            Objects.equals(deadline, that.deadline) &&
            Objects.equals(archived, that.archived) &&
            Objects.equals(archivedDate, that.archivedDate) &&
            Objects.equals(workGroupId, that.workGroupId) &&
            Objects.equals(priorityId, that.priorityId) &&
            Objects.equals(statusId, that.statusId) &&
            Objects.equals(parentProjectId, that.parentProjectId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            description,
            createTime,
            updateTime,
            deadline,
            archived,
            archivedDate,
            workGroupId,
            priorityId,
            statusId,
            parentProjectId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalCreateTime().map(f -> "createTime=" + f + ", ").orElse("") +
            optionalUpdateTime().map(f -> "updateTime=" + f + ", ").orElse("") +
            optionalDeadline().map(f -> "deadline=" + f + ", ").orElse("") +
            optionalArchived().map(f -> "archived=" + f + ", ").orElse("") +
            optionalArchivedDate().map(f -> "archivedDate=" + f + ", ").orElse("") +
            optionalWorkGroupId().map(f -> "workGroupId=" + f + ", ").orElse("") +
            optionalPriorityId().map(f -> "priorityId=" + f + ", ").orElse("") +
            optionalStatusId().map(f -> "statusId=" + f + ", ").orElse("") +
            optionalParentProjectId().map(f -> "parentProjectId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
