package com.dcmc.apps.taskmanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "create_time", nullable = false)
    private Instant createTime = Instant.now();

    @NotNull
    @Column(name = "update_time", nullable = false)
    private Instant updateTime;

    @Column(name = "deadline")
    private ZonedDateTime deadline;

    @Column(name = "archived")
    private Boolean archived = false;

    @Column(name = "archived_date")
    private ZonedDateTime archivedDate;

    @ManyToOne(optional = false)
    @NotNull
    private WorkGroup workGroup;

    @ManyToOne(optional = false)
    @NotNull
    private Priority priority;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "workGroup", "createdBy" }, allowSetters = true)
    private TaskStatusCatalog status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "workGroup" }, allowSetters = true)
    private Project parentProject;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "task", "author" }, allowSetters = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "task", "user" }, allowSetters = true)
    private List<TaskAssignment> taskAssignments = new ArrayList<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Task id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Task title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Task description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreateTime() {
        return this.createTime;
    }

    public Task createTime(Instant createTime) {
        this.setCreateTime(createTime);
        return this;
    }

    public void setCreateTime(Instant createTime) {
        this.createTime = createTime;
    }

    public Instant getUpdateTime() {
        return this.updateTime;
    }

    public Task updateTime(Instant updateTime) {
        this.setUpdateTime(updateTime);
        return this;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public ZonedDateTime getDeadline() {
        return this.deadline;
    }

    public Task deadline(ZonedDateTime deadline) {
        this.setDeadline(deadline);
        return this;
    }

    public void setDeadline(ZonedDateTime deadline) {
        this.deadline = deadline;
    }

    public Boolean getArchived() {
        return this.archived;
    }

    public Task archived(Boolean archived) {
        this.setArchived(archived);
        return this;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public ZonedDateTime getArchivedDate() {
        return this.archivedDate;
    }

    public Task archivedDate(ZonedDateTime archivedDate) {
        this.setArchivedDate(archivedDate);
        return this;
    }

    public void setArchivedDate(ZonedDateTime archivedDate) {
        this.archivedDate = archivedDate;
    }

    public WorkGroup getWorkGroup() {
        return this.workGroup;
    }

    public void setWorkGroup(WorkGroup workGroup) {
        this.workGroup = workGroup;
    }

    public Task workGroup(WorkGroup workGroup) {
        this.setWorkGroup(workGroup);
        return this;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Task priority(Priority priority) {
        this.setPriority(priority);
        return this;
    }

    public TaskStatusCatalog getStatus() {
        return this.status;
    }

    public void setStatus(TaskStatusCatalog taskStatusCatalog) {
        this.status = taskStatusCatalog;
    }

    public Task status(TaskStatusCatalog taskStatusCatalog) {
        this.setStatus(taskStatusCatalog);
        return this;
    }

    public Project getParentProject() {
        return this.parentProject;
    }

    public void setParentProject(Project project) {
        this.parentProject = project;
    }

    public Task parentProject(Project project) {
        this.setParentProject(project);
        return this;
    }

    public List<Comment> getComments() {
        return this.comments;
    }

    public void setComments(List<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setTask(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setTask(this));
        }
        this.comments = comments;
    }

    public Task comments(List<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Task addComment(Comment comment) {
        this.comments.add(comment);
        comment.setTask(this);
        return this;
    }

    public Task removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setTask(null);
        return this;
    }

    public List<TaskAssignment> getTaskAssignments() {
        return this.taskAssignments;
    }

    public void setTaskAssignments(List<TaskAssignment> taskAssignments) {
        if (this.taskAssignments != null) {
            this.taskAssignments.forEach(i -> i.setTask(null));
        }
        if (taskAssignments != null) {
            taskAssignments.forEach(i -> i.setTask(this));
        }
        this.taskAssignments = taskAssignments;
    }

    public Task taskAssignments(List<TaskAssignment> taskAssignments) {
        this.setTaskAssignments(taskAssignments);
        return this;
    }

    public Task addTaskAssignment(TaskAssignment taskAssignment) {
        this.taskAssignments.add(taskAssignment);
        taskAssignment.setTask(this);
        return this;
    }

    public Task removeTaskAssignment(TaskAssignment taskAssignment) {
        this.taskAssignments.remove(taskAssignment);
        taskAssignment.setTask(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return getId() != null && getId().equals(((Task) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", createTime='" + getCreateTime() + "'" +
            ", updateTime='" + getUpdateTime() + "'" +
            ", deadline='" + getDeadline() + "'" +
            ", archived='" + getArchived() + "'" +
            ", archivedDate='" + getArchivedDate() + "'" +
            "}";
    }
}
