package com.dcmc.apps.taskmanager.web.rest;

import com.dcmc.apps.taskmanager.repository.TaskRepository;
import com.dcmc.apps.taskmanager.service.TaskQueryService;
import com.dcmc.apps.taskmanager.service.TaskService;
import com.dcmc.apps.taskmanager.service.criteria.TaskCriteria;
import com.dcmc.apps.taskmanager.service.dto.CommentDTO;
import com.dcmc.apps.taskmanager.service.dto.CreateTaskDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskAssignmentDTO;
import com.dcmc.apps.taskmanager.service.dto.TaskDTO;
import com.dcmc.apps.taskmanager.web.rest.errors.BadRequestAlertException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dcmc.apps.taskmanager.domain.Task}.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskResource.class);

    private static final String ENTITY_NAME = "taskmanagerTask";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskService taskService;

    private final TaskRepository taskRepository;

    private final TaskQueryService taskQueryService;

    public TaskResource(TaskService taskService, TaskRepository taskRepository, TaskQueryService taskQueryService) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.taskQueryService = taskQueryService;
    }

    /**
     * {@code POST  /tasks} : Create a new task.
     *
     * @param taskDTO the taskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskDTO, or with status {@code 400 (Bad Request)} if the task has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Hidden
    @PostMapping("")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) throws URISyntaxException {
        LOG.debug("REST request to save Task : {}", taskDTO);
        if (taskDTO.getId() != null) {
            throw new BadRequestAlertException("A new task cannot already have an ID", ENTITY_NAME, "idexists");
        }
        taskDTO = taskService.save(taskDTO);
        return ResponseEntity.created(new URI("/api/tasks/" + taskDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, taskDTO.getId().toString()))
            .body(taskDTO);
    }

    /**
     * {@code PUT  /tasks/:id} : Updates an existing task.
     *
     * @param id the id of the taskDTO to save.
     * @param taskDTO the taskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskDTO,
     * or with status {@code 400 (Bad Request)} if the taskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CreateTaskDTO taskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Task : {}, {}", id, taskDTO);


        TaskDTO result = taskService.update(id, taskDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tasks/:id} : Partial updates given fields of an existing task, field will ignore if it is null
     *
     * @param id the id of the taskDTO to save.
     * @param taskDTO the taskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskDTO,
     * or with status {@code 400 (Bad Request)} if the taskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Hidden
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskDTO> partialUpdateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaskDTO taskDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Task partially : {}, {}", id, taskDTO);
        if (taskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskDTO> result = taskService.partialUpdate(taskDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, taskDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tasks} : get all the tasks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tasks in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> getAllTasks(
        TaskCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Tasks by criteria: {}", criteria);

        criteria.withArchived(false);

        Page<TaskDTO> page = taskQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tasks/count} : count all the tasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTasks(TaskCriteria criteria) {
        LOG.debug("REST request to count Tasks by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tasks/:id} : get the "id" task.
     *
     * @param id the id of the taskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskDTO, or with status {@code 404 (Not Found)}.
     */
    @Hidden
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Task : {}", id);
        Optional<TaskDTO> taskDTO = taskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskDTO);
    }

    @GetMapping("/{id}/assignments")
    public ResponseEntity<List<TaskAssignmentDTO>> getTaskAssignments(
        @PathVariable Long taskId) {

        List<TaskAssignmentDTO> assignments = taskService.getTaskAssignments(taskId);
        return ResponseEntity.ok(assignments);
    }

    /**
     * {@code DELETE  /tasks/:id} : delete the "id" task.
     *
     * @param id the id of the taskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Hidden
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Task : {}", id);
        taskService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentDTO> addCommentToTask(
        @PathVariable Long taskId,
        @RequestBody String content) {

        LOG.debug("REST request to add comment to task {}: {}", taskId, content);
        CommentDTO result = taskService.addCommentToTask(taskId, content);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentDTO>> getTaskComments(
        @PathVariable Long taskId) {

        LOG.debug("REST request to get comments for task {}", taskId);
        List<CommentDTO> comments = taskService.getTaskComments(taskId);
        return ResponseEntity.ok(comments);
    }

    // *******************************************************************************************************************

    @GetMapping("/archived-tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasksArchived(
        TaskCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Tasks by criteria: {}", criteria);

        criteria.withArchived(true);

        Page<TaskDTO> page = taskQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @DeleteMapping("/archived-tasks/{id}")
    public ResponseEntity<Void> deleteArchivedTask(@PathVariable Long id) {
        taskService.deleteArchivedTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assign/{userLogin}")
    public ResponseEntity<Void> assignTask(
        @PathVariable Long id,
        @PathVariable String userLogin
    ) {
        taskService.assignTask(id, userLogin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/unassign/{userLogin}")
    public ResponseEntity<Void> unassignTask(
        @PathVariable Long id,
        @PathVariable String userLogin
    ) {
        taskService.unassignTask(id, userLogin);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/priority")
    public ResponseEntity<Void> changePriority(
        @PathVariable Long id,
        @RequestParam String priority
    ) {
        taskService.changePriority(id, priority);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> changeStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        taskService.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveTask(@PathVariable Long id) {
        taskService.archiveTask(id);
        return ResponseEntity.ok().build();
    }
}
