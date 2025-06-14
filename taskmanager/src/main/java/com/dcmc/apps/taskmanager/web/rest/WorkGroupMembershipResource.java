package com.dcmc.apps.taskmanager.web.rest;

import com.dcmc.apps.taskmanager.repository.WorkGroupMembershipRepository;
import com.dcmc.apps.taskmanager.service.WorkGroupMembershipQueryService;
import com.dcmc.apps.taskmanager.service.WorkGroupMembershipService;
import com.dcmc.apps.taskmanager.service.criteria.WorkGroupMembershipCriteria;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupMembershipDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dcmc.apps.taskmanager.domain.WorkGroupMembership}.
 */
@RestController
@RequestMapping("/api/work-group-memberships")
public class WorkGroupMembershipResource {

    private static final Logger LOG = LoggerFactory.getLogger(WorkGroupMembershipResource.class);

    private static final String ENTITY_NAME = "taskmanagerWorkGroupMembership";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkGroupMembershipService workGroupMembershipService;

    private final WorkGroupMembershipRepository workGroupMembershipRepository;

    private final WorkGroupMembershipQueryService workGroupMembershipQueryService;

    public WorkGroupMembershipResource(
        WorkGroupMembershipService workGroupMembershipService,
        WorkGroupMembershipRepository workGroupMembershipRepository,
        WorkGroupMembershipQueryService workGroupMembershipQueryService
    ) {
        this.workGroupMembershipService = workGroupMembershipService;
        this.workGroupMembershipRepository = workGroupMembershipRepository;
        this.workGroupMembershipQueryService = workGroupMembershipQueryService;
    }

    /**
     * {@code POST  /work-group-memberships} : Create a new workGroupMembership.
     *
     * @param workGroupMembershipDTO the workGroupMembershipDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workGroupMembershipDTO, or with status {@code 400 (Bad Request)} if the workGroupMembership has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WorkGroupMembershipDTO> createWorkGroupMembership(
        @Valid @RequestBody WorkGroupMembershipDTO workGroupMembershipDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save WorkGroupMembership : {}", workGroupMembershipDTO);
        if (workGroupMembershipDTO.getId() != null) {
            throw new BadRequestAlertException("A new workGroupMembership cannot already have an ID", ENTITY_NAME, "idexists");
        }
        workGroupMembershipDTO = workGroupMembershipService.save(workGroupMembershipDTO);
        return ResponseEntity.created(new URI("/api/work-group-memberships/" + workGroupMembershipDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, workGroupMembershipDTO.getId().toString()))
            .body(workGroupMembershipDTO);
    }

    /**
     * {@code PUT  /work-group-memberships/:id} : Updates an existing workGroupMembership.
     *
     * @param id the id of the workGroupMembershipDTO to save.
     * @param workGroupMembershipDTO the workGroupMembershipDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workGroupMembershipDTO,
     * or with status {@code 400 (Bad Request)} if the workGroupMembershipDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workGroupMembershipDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WorkGroupMembershipDTO> updateWorkGroupMembership(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkGroupMembershipDTO workGroupMembershipDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WorkGroupMembership : {}, {}", id, workGroupMembershipDTO);
        if (workGroupMembershipDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workGroupMembershipDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workGroupMembershipRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        workGroupMembershipDTO = workGroupMembershipService.update(workGroupMembershipDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workGroupMembershipDTO.getId().toString()))
            .body(workGroupMembershipDTO);
    }

    /**
     * {@code PATCH  /work-group-memberships/:id} : Partial updates given fields of an existing workGroupMembership, field will ignore if it is null
     *
     * @param id the id of the workGroupMembershipDTO to save.
     * @param workGroupMembershipDTO the workGroupMembershipDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workGroupMembershipDTO,
     * or with status {@code 400 (Bad Request)} if the workGroupMembershipDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workGroupMembershipDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workGroupMembershipDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkGroupMembershipDTO> partialUpdateWorkGroupMembership(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkGroupMembershipDTO workGroupMembershipDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update WorkGroupMembership partially : {}, {}", id, workGroupMembershipDTO);
        if (workGroupMembershipDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workGroupMembershipDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workGroupMembershipRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkGroupMembershipDTO> result = workGroupMembershipService.partialUpdate(workGroupMembershipDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workGroupMembershipDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /work-group-memberships} : get all the workGroupMemberships.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workGroupMemberships in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WorkGroupMembershipDTO>> getAllWorkGroupMemberships(WorkGroupMembershipCriteria criteria) {
        LOG.debug("REST request to get WorkGroupMemberships by criteria: {}", criteria);

        List<WorkGroupMembershipDTO> entityList = workGroupMembershipQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /work-group-memberships/count} : count all the workGroupMemberships.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countWorkGroupMemberships(WorkGroupMembershipCriteria criteria) {
        LOG.debug("REST request to count WorkGroupMemberships by criteria: {}", criteria);
        return ResponseEntity.ok().body(workGroupMembershipQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /work-group-memberships/:id} : get the "id" workGroupMembership.
     *
     * @param id the id of the workGroupMembershipDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workGroupMembershipDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WorkGroupMembershipDTO> getWorkGroupMembership(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WorkGroupMembership : {}", id);
        Optional<WorkGroupMembershipDTO> workGroupMembershipDTO = workGroupMembershipService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workGroupMembershipDTO);
    }

    /**
     * {@code DELETE  /work-group-memberships/:id} : delete the "id" workGroupMembership.
     *
     * @param id the id of the workGroupMembershipDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkGroupMembership(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WorkGroupMembership : {}", id);
        workGroupMembershipService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
