package com.dcmc.apps.taskmanager.web.rest;

import com.dcmc.apps.taskmanager.repository.WorkGroupRepository;
import com.dcmc.apps.taskmanager.security.SecurityUtils;
import com.dcmc.apps.taskmanager.service.WorkGroupQueryService;
import com.dcmc.apps.taskmanager.service.WorkGroupService;
import com.dcmc.apps.taskmanager.service.criteria.WorkGroupCriteria;
import com.dcmc.apps.taskmanager.service.dto.*;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dcmc.apps.taskmanager.domain.WorkGroup}.
 */
@RestController
@RequestMapping("/api/work-groups")
public class WorkGroupResource {

    private static final Logger LOG = LoggerFactory.getLogger(WorkGroupResource.class);

    private static final String ENTITY_NAME = "taskmanagerWorkGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WorkGroupService workGroupService;

    private final WorkGroupRepository workGroupRepository;

    private final WorkGroupQueryService workGroupQueryService;

    public WorkGroupResource(
        WorkGroupService workGroupService,
        WorkGroupRepository workGroupRepository,
        WorkGroupQueryService workGroupQueryService
    ) {
        this.workGroupService = workGroupService;
        this.workGroupRepository = workGroupRepository;
        this.workGroupQueryService = workGroupQueryService;
    }

    /**
     * {@code POST  /work-groups} : Create a new workGroup.
     *
     * @param workGroupDTO the workGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new workGroupDTO, or with status {@code 400 (Bad Request)} if the workGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WorkGroupDTO> createWorkGroup(@Valid @RequestBody CreateWorkGroupDTO workGroupDTO) throws URISyntaxException {
        LOG.debug("REST request to save WorkGroup : {}", workGroupDTO);

        WorkGroupDTO result = workGroupService.save(workGroupDTO);

        return ResponseEntity.created(new URI("/api/work-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /work-groups/:id} : Updates an existing workGroup.
     *
     * @param id the id of the workGroupDTO to save.
     * @param workGroupDTO the workGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workGroupDTO,
     * or with status {@code 400 (Bad Request)} if the workGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the workGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    @Hidden
    @PutMapping("/{id}")
    public ResponseEntity<WorkGroupDTO> updateWorkGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WorkGroupDTO workGroupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update WorkGroup : {}, {}", id, workGroupDTO);
        if (workGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        workGroupDTO = workGroupService.update(workGroupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workGroupDTO.getId().toString()))
            .body(workGroupDTO);
    }

    /**
     * {@code PATCH  /work-groups/:id} : Partial updates given fields of an existing workGroup, field will ignore if it is null
     *
     * @param id the id of the workGroupDTO to save.
     * @param workGroupDTO the workGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated workGroupDTO,
     * or with status {@code 400 (Bad Request)} if the workGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the workGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the workGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */

    @Hidden
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WorkGroupDTO> partialUpdateWorkGroup(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WorkGroupDTO workGroupDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update WorkGroup partially : {}, {}", id, workGroupDTO);
        if (workGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, workGroupDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!workGroupRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WorkGroupDTO> result = workGroupService.partialUpdate(workGroupDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, workGroupDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /work-groups} : get all the workGroups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of workGroups in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WorkGroupDTO>> getAllWorkGroups(
        WorkGroupCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get WorkGroups by criteria: {}", criteria);

        Page<WorkGroupDTO> page = workGroupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /work-groups/count} : count all the workGroups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */

    @Hidden
    @GetMapping("/count")
    public ResponseEntity<Long> countWorkGroups(WorkGroupCriteria criteria) {
        LOG.debug("REST request to count WorkGroups by criteria: {}", criteria);
        return ResponseEntity.ok().body(workGroupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /work-groups/:id} : get the "id" workGroup.
     *
     * @param id the id of the workGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the workGroupDTO, or with status {@code 404 (Not Found)}.
     */

    @Hidden
    @GetMapping("/{id}")
    public ResponseEntity<WorkGroupDTO> getWorkGroup(@PathVariable("id") Long id) {
        LOG.debug("REST request to get WorkGroup : {}", id);
        Optional<WorkGroupDTO> workGroupDTO = workGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(workGroupDTO);
    }

    /**
     * {@code DELETE  /work-groups/:id} : delete the "id" workGroup.
     *
     * @param id the id of the workGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */

    @Hidden
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkGroup(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete WorkGroup : {}", id);
        workGroupService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

//    ****************************************************************************************************************

    /**
     * {@code PUT /work-groups/{id}/transfer-ownership} : Transfiere la propiedad de un grupo a otro usuario.
     *
     * @param id el id del grupo a transferir.
     * @param newOwnerUserId contiene el ID del nuevo propietario.
     * @return el {@link ResponseEntity} con status {@code 200 (OK)} si la transferencia fue exitosa,
     *         o con status {@code 400 (Bad Request)} si los datos son inválidos,
     *         o con status {@code 404 (Not Found)} si el grupo o usuario no existen,
     *         o con status {@code 500 (Internal Server Error)} si ocurre un error inesperado.
     */
    @PutMapping("/{id}/transfer-ownership/{newOwnerUserId}")
    public ResponseEntity<Void> transferOwnership(
        @PathVariable final Long id,
        @PathVariable String newOwnerUserId
    ) {
        LOG.debug("REST request to transfer ownership of WorkGroup : {}, {}", id, newOwnerUserId);



        workGroupService.transferOwnership(id, newOwnerUserId);

        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "Se transfirió la propiedad correctamente", id.toString()))
            .build();
    }

    /**
     * {@code POST /work-groups/:id/moderators} : Agrega un moderador al grupo.
     * @param id ID del grupo
     * @param moderatorActionDTO DTO con el ID del usuario
     * @return ResponseEntity con status 200 (OK)
     */
    @PostMapping("/{id}/moderators")
    public ResponseEntity<Void> addModerator(
        @PathVariable Long id,
        @Valid @RequestBody ModeratorActionDTO moderatorActionDTO) {

        workGroupService.addModerator(id, moderatorActionDTO.getUserId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "workGroup.moderator.added", id.toString()))
            .build();
    }

    /**
     * {@code DELETE /work-groups/:id/moderators} : Elimina un moderador del grupo.
     * @param id ID del grupo
     * @param moderatorActionDTO DTO con el ID del usuario
     * @return ResponseEntity con status 200 (OK)
     */
    @DeleteMapping("/{id}/moderators")
    public ResponseEntity<Void> removeModerator(
        @PathVariable Long id,
        @Valid @RequestBody ModeratorActionDTO moderatorActionDTO) {

        workGroupService.removeModerator(id, moderatorActionDTO.getUserId());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "workGroup.moderator.removed", id.toString()))
            .build();
    }


    /**
     * {@code POST /work-groups/{id}/members} : Agrega un miembro al grupo.
     * Solo OWNER y MODERADOR pueden realizar esta acción.
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addMember(
        @PathVariable Long id,
        @Valid @RequestBody MemberActionDTO memberActionDTO) {

        workGroupService.addMember(id, memberActionDTO.getUserLogin());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "workGroup.member.added", id.toString()))
            .build();
    }

    /**
     * {@code DELETE /work-groups/{id}/members} : Elimina un miembro del grupo.
     * Solo OWNER y MODERADOR pueden realizar esta acción.
     */
    @DeleteMapping("/{id}/members")
    public ResponseEntity<Void> removeMember(
        @PathVariable Long id,
        @Valid @RequestBody MemberActionDTO memberActionDTO) {

        workGroupService.removeMember(id, memberActionDTO.getUserLogin());
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert(applicationName, "workGroup.member.removed", id.toString()))
            .build();
    }

    /**
     * {@code GET /work-groups/{id}/members} : Obtiene todos los miembros de un grupo con sus roles.
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<MemberWithRoleDTO>> getAllMembersWithRoles(@PathVariable Long id) {
        LOG.debug("REST request to get all members with roles for work group {}", id);
        List<MemberWithRoleDTO> members = workGroupService.getAllMembersWithRoles(id);
        return ResponseEntity.ok().body(members);
    }

    /**
     * {@code DELETE /work-groups/{id}/leave} : Permite a un usuario salir de un grupo.
     */
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveWorkGroup(@PathVariable Long id) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccessDeniedException("User not logged in"));

        workGroupService.leaveWorkGroup(id, userLogin);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createAlert(applicationName, "workGroup.left", id.toString()))
            .build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<UserWorkGroupDTO>> getCurrentUserWorkGroups() {
        LOG.debug("REST request to get WorkGroups for current user");
        List<UserWorkGroupDTO> workGroups = workGroupService.getCurrentUserWorkGroups();
        return ResponseEntity.ok(workGroups);
    }


}
