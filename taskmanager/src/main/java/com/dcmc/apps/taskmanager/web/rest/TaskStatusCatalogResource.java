package com.dcmc.apps.taskmanager.web.rest;

import com.dcmc.apps.taskmanager.repository.TaskStatusCatalogRepository;
import com.dcmc.apps.taskmanager.service.TaskStatusCatalogQueryService;
import com.dcmc.apps.taskmanager.service.TaskStatusCatalogService;
import com.dcmc.apps.taskmanager.service.criteria.TaskStatusCatalogCriteria;
import com.dcmc.apps.taskmanager.service.dto.TaskStatusCatalogDTO;
import com.dcmc.apps.taskmanager.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.dcmc.apps.taskmanager.domain.TaskStatusCatalog}.
 */
@RestController
@RequestMapping("/api/task-status-catalogs")
public class TaskStatusCatalogResource {

    private static final Logger LOG = LoggerFactory.getLogger(TaskStatusCatalogResource.class);

    private static final String ENTITY_NAME = "taskmanagerTaskStatusCatalog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskStatusCatalogService taskStatusCatalogService;

    private final TaskStatusCatalogRepository taskStatusCatalogRepository;

    private final TaskStatusCatalogQueryService taskStatusCatalogQueryService;

    public TaskStatusCatalogResource(
        TaskStatusCatalogService taskStatusCatalogService,
        TaskStatusCatalogRepository taskStatusCatalogRepository,
        TaskStatusCatalogQueryService taskStatusCatalogQueryService
    ) {
        this.taskStatusCatalogService = taskStatusCatalogService;
        this.taskStatusCatalogRepository = taskStatusCatalogRepository;
        this.taskStatusCatalogQueryService = taskStatusCatalogQueryService;
    }

    /**
     * {@code POST  /task-status-catalogs} : Create a new taskStatusCatalog.
     *
     * @param taskStatusCatalogDTO the taskStatusCatalogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskStatusCatalogDTO, or with status {@code 400 (Bad Request)} if the taskStatusCatalog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TaskStatusCatalogDTO> createTaskStatusCatalog(@Valid @RequestBody TaskStatusCatalogDTO taskStatusCatalogDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save TaskStatusCatalog : {}", taskStatusCatalogDTO);
        if (taskStatusCatalogDTO.getId() != null) {
            throw new BadRequestAlertException("A new taskStatusCatalog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        taskStatusCatalogDTO = taskStatusCatalogService.save(taskStatusCatalogDTO);
        return ResponseEntity.created(new URI("/api/task-status-catalogs/" + taskStatusCatalogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, taskStatusCatalogDTO.getId().toString()))
            .body(taskStatusCatalogDTO);
    }

    /**
     * {@code PUT  /task-status-catalogs/:id} : Updates an existing taskStatusCatalog.
     *
     * @param id the id of the taskStatusCatalogDTO to save.
     * @param taskStatusCatalogDTO the taskStatusCatalogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskStatusCatalogDTO,
     * or with status {@code 400 (Bad Request)} if the taskStatusCatalogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskStatusCatalogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusCatalogDTO> updateTaskStatusCatalog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TaskStatusCatalogDTO taskStatusCatalogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TaskStatusCatalog : {}, {}", id, taskStatusCatalogDTO);
        if (taskStatusCatalogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskStatusCatalogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskStatusCatalogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        taskStatusCatalogDTO = taskStatusCatalogService.update(taskStatusCatalogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, taskStatusCatalogDTO.getId().toString()))
            .body(taskStatusCatalogDTO);
    }

    /**
     * {@code PATCH  /task-status-catalogs/:id} : Partial updates given fields of an existing taskStatusCatalog, field will ignore if it is null
     *
     * @param id the id of the taskStatusCatalogDTO to save.
     * @param taskStatusCatalogDTO the taskStatusCatalogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskStatusCatalogDTO,
     * or with status {@code 400 (Bad Request)} if the taskStatusCatalogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taskStatusCatalogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskStatusCatalogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskStatusCatalogDTO> partialUpdateTaskStatusCatalog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TaskStatusCatalogDTO taskStatusCatalogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TaskStatusCatalog partially : {}, {}", id, taskStatusCatalogDTO);
        if (taskStatusCatalogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskStatusCatalogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskStatusCatalogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskStatusCatalogDTO> result = taskStatusCatalogService.partialUpdate(taskStatusCatalogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, taskStatusCatalogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /task-status-catalogs} : get all the taskStatusCatalogs.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskStatusCatalogs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TaskStatusCatalogDTO>> getAllTaskStatusCatalogs(
        TaskStatusCatalogCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get TaskStatusCatalogs by criteria: {}", criteria);

        Page<TaskStatusCatalogDTO> page = taskStatusCatalogQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-status-catalogs/count} : count all the taskStatusCatalogs.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTaskStatusCatalogs(TaskStatusCatalogCriteria criteria) {
        LOG.debug("REST request to count TaskStatusCatalogs by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskStatusCatalogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /task-status-catalogs/:id} : get the "id" taskStatusCatalog.
     *
     * @param id the id of the taskStatusCatalogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskStatusCatalogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusCatalogDTO> getTaskStatusCatalog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TaskStatusCatalog : {}", id);
        Optional<TaskStatusCatalogDTO> taskStatusCatalogDTO = taskStatusCatalogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskStatusCatalogDTO);
    }

    /**
     * {@code DELETE  /task-status-catalogs/:id} : delete the "id" taskStatusCatalog.
     *
     * @param id the id of the taskStatusCatalogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatusCatalog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TaskStatusCatalog : {}", id);
        taskStatusCatalogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
