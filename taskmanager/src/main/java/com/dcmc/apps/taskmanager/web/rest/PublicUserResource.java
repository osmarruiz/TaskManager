package com.dcmc.apps.taskmanager.web.rest;

import com.dcmc.apps.taskmanager.service.UserService;
import com.dcmc.apps.taskmanager.service.WorkGroupService;
import com.dcmc.apps.taskmanager.service.dto.UserDTO;
import java.util.*;

import com.dcmc.apps.taskmanager.service.dto.UserWorkGroupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

@RestController
@RequestMapping("/api")
public class PublicUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(PublicUserResource.class);

    private final UserService userService;

    private final WorkGroupService workGroupService;


    public PublicUserResource(UserService userService, WorkGroupService workGroupService) {
        this.userService = userService;
        this.workGroupService = workGroupService;
    }

    /**
     * {@code GET /users} : get all users with only public information - calling this method is allowed for anyone.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllPublicUsers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get all public User names");

        final Page<UserDTO> page = userService.getAllPublicUsers(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * {@code GET /users/{login}/work-groups} : Obtiene todos los grupos de un usuario.
     */
    @GetMapping("/{login}/work-groups")
    public ResponseEntity<List<UserWorkGroupDTO>> getUserWorkGroups(@PathVariable String login) {
        LOG.debug("REST request to get work groups for user {}", login);
        List<UserWorkGroupDTO> workGroups = workGroupService.getUserWorkGroups(login);
        return ResponseEntity.ok().body(workGroups);
    }
}
