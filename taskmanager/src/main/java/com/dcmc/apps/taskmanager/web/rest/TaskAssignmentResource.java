package com.dcmc.apps.taskmanager.web.rest;

import com.dcmc.apps.taskmanager.repository.TaskAssignmentRepository;
import com.dcmc.apps.taskmanager.service.TaskAssignmentQueryService;
import com.dcmc.apps.taskmanager.service.TaskAssignmentService;
import com.dcmc.apps.taskmanager.service.criteria.TaskAssignmentCriteria;
import com.dcmc.apps.taskmanager.service.dto.TaskAssignmentDTO;
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
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dcmc.apps.taskmanager.domain.TaskAssignment}.
 */
@Hidden
@RestController
@RequestMapping("/api/task-assignments")
public class TaskAssignmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskAssignmentResource.class);

    private static final String ENTITY_NAME = "taskmanagerTaskAssignment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskAssignmentService taskAssignmentService;

    private final TaskAssignmentRepository taskAssignmentRepository;

    private final TaskAssignmentQueryService taskAssignmentQueryService;

    public TaskAssignmentResource(
        TaskAssignmentService taskAssignmentService,
        TaskAssignmentRepository taskAssignmentRepository,
        TaskAssignmentQueryService taskAssignmentQueryService
    ) {
        this.taskAssignmentService = taskAssignmentService;
        this.taskAssignmentRepository = taskAssignmentRepository;
        this.taskAssignmentQueryService = taskAssignmentQueryService;
    }

    /**
     * {@code POST  /task-assignments} : Create a new taskAssignment.
     *
     * @param taskAssignmentDTO the taskAssignmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskAssignmentDTO, or with status {@code 400 (Bad Request)} if the taskAssignment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TaskAssignmentDTO> createTaskAssignment(@Valid @RequestBody TaskAssignmentDTO taskAssignmentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TaskAssignment : {}", taskAssignmentDTO);
        if (taskAssignmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new taskAssignment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        taskAssignmentDTO = taskAssignmentService.save(taskAssignmentDTO);
        return ResponseEntity.created(new URI("/api/task-assignments/" + taskAssignmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, taskAssignmentDTO.getId().toString()))
            .body(taskAssignmentDTO);
    }

    /**
     * {@code PUT  /task-assignments/:id} : Updates an existing taskAssignment.
     *
     * @param id the id of the taskAssignmentDTO to save.
     * @param taskAssignmentDTO the taskAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the taskAssignmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskAssignmentDTO> updateTaskAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaskAssignmentDTO taskAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TaskAssignment : {}, {}", id, taskAssignmentDTO);
        if (taskAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskAssignmentDTO = taskAssignmentService.update(taskAssignmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, taskAssignmentDTO.getId().toString()))
            .body(taskAssignmentDTO);
    }

    /**
     * {@code PATCH  /task-assignments/:id} : Partial updates given fields of an existing taskAssignment, field will ignore if it is null
     *
     * @param id the id of the taskAssignmentDTO to save.
     * @param taskAssignmentDTO the taskAssignmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskAssignmentDTO,
     * or with status {@code 400 (Bad Request)} if the taskAssignmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taskAssignmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskAssignmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskAssignmentDTO> partialUpdateTaskAssignment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaskAssignmentDTO taskAssignmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaskAssignment partially : {}, {}", id, taskAssignmentDTO);
        if (taskAssignmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskAssignmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskAssignmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskAssignmentDTO> result = taskAssignmentService.partialUpdate(taskAssignmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, taskAssignmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /task-assignments} : get all the taskAssignments.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskAssignments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaskAssignmentDTO>> getAllTaskAssignments(
        TaskAssignmentCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TaskAssignments by criteria: {}", criteria);

        Page<TaskAssignmentDTO> page = taskAssignmentQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-assignments/count} : count all the taskAssignments.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTaskAssignments(TaskAssignmentCriteria criteria) {
        LOG.debug("REST request to count TaskAssignments by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskAssignmentQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /task-assignments/:id} : get the "id" taskAssignment.
     *
     * @param id the id of the taskAssignmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskAssignmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskAssignmentDTO> getTaskAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TaskAssignment : {}", id);
        Optional<TaskAssignmentDTO> taskAssignmentDTO = taskAssignmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskAssignmentDTO);
    }

    /**
     * {@code DELETE  /task-assignments/:id} : delete the "id" taskAssignment.
     *
     * @param id the id of the taskAssignmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskAssignment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TaskAssignment : {}", id);
        taskAssignmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
