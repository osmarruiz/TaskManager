package com.dcmc.apps.taskmanager.web.rest;

import com.dcmc.apps.taskmanager.repository.PriorityRepository;
import com.dcmc.apps.taskmanager.service.PriorityQueryService;
import com.dcmc.apps.taskmanager.service.PriorityService;
import com.dcmc.apps.taskmanager.service.criteria.PriorityCriteria;
import com.dcmc.apps.taskmanager.service.dto.PriorityDTO;
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
 * REST controller for managing {@link com.dcmc.apps.taskmanager.domain.Priority}.
 */
@RestController
@RequestMapping("/api/priorities")
public class PriorityResource {

    private static final Logger LOG = LoggerFactory.getLogger(PriorityResource.class);

    private static final String ENTITY_NAME = "taskmanagerPriority";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PriorityService priorityService;

    private final PriorityRepository priorityRepository;

    private final PriorityQueryService priorityQueryService;

    public PriorityResource(
        PriorityService priorityService,
        PriorityRepository priorityRepository,
        PriorityQueryService priorityQueryService
    ) {
        this.priorityService = priorityService;
        this.priorityRepository = priorityRepository;
        this.priorityQueryService = priorityQueryService;
    }

    /**
     * {@code POST  /priorities} : Create a new priority.
     *
     * @param priorityDTO the priorityDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new priorityDTO, or with status {@code 400 (Bad Request)} if the priority has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PriorityDTO> createPriority(@Valid @RequestBody PriorityDTO priorityDTO) throws URISyntaxException {
        LOG.debug("REST request to save Priority : {}", priorityDTO);
        if (priorityDTO.getId() != null) {
            throw new BadRequestAlertException("A new priority cannot already have an ID", ENTITY_NAME, "idexists");
        }
        priorityDTO = priorityService.save(priorityDTO);
        return ResponseEntity.created(new URI("/api/priorities/" + priorityDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, priorityDTO.getId().toString()))
            .body(priorityDTO);
    }

    /**
     * {@code PUT  /priorities/:id} : Updates an existing priority.
     *
     * @param id the id of the priorityDTO to save.
     * @param priorityDTO the priorityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated priorityDTO,
     * or with status {@code 400 (Bad Request)} if the priorityDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the priorityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PriorityDTO> updatePriority(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PriorityDTO priorityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Priority : {}, {}", id, priorityDTO);
        if (priorityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, priorityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!priorityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        priorityDTO = priorityService.update(priorityDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, priorityDTO.getId().toString()))
            .body(priorityDTO);
    }

    /**
     * {@code PATCH  /priorities/:id} : Partial updates given fields of an existing priority, field will ignore if it is null
     *
     * @param id the id of the priorityDTO to save.
     * @param priorityDTO the priorityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated priorityDTO,
     * or with status {@code 400 (Bad Request)} if the priorityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the priorityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the priorityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PriorityDTO> partialUpdatePriority(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PriorityDTO priorityDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Priority partially : {}, {}", id, priorityDTO);
        if (priorityDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, priorityDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!priorityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PriorityDTO> result = priorityService.partialUpdate(priorityDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, priorityDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /priorities} : get all the priorities.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of priorities in body.
     */
    @GetMapping("")
    public ResponseEntity<List<PriorityDTO>> getAllPriorities(
        PriorityCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get Priorities by criteria: {}", criteria);

        Page<PriorityDTO> page = priorityQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /priorities/count} : count all the priorities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countPriorities(PriorityCriteria criteria) {
        LOG.debug("REST request to count Priorities by criteria: {}", criteria);
        return ResponseEntity.ok().body(priorityQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /priorities/:id} : get the "id" priority.
     *
     * @param id the id of the priorityDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the priorityDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PriorityDTO> getPriority(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Priority : {}", id);
        Optional<PriorityDTO> priorityDTO = priorityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(priorityDTO);
    }

    /**
     * {@code DELETE  /priorities/:id} : delete the "id" priority.
     *
     * @param id the id of the priorityDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriority(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Priority : {}", id);
        priorityService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
