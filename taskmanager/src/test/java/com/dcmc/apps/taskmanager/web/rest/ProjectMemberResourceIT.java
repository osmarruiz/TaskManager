package com.dcmc.apps.taskmanager.web.rest;

import static com.dcmc.apps.taskmanager.domain.ProjectMemberAsserts.*;
import static com.dcmc.apps.taskmanager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dcmc.apps.taskmanager.IntegrationTest;
import com.dcmc.apps.taskmanager.domain.Project;
import com.dcmc.apps.taskmanager.domain.ProjectMember;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.repository.ProjectMemberRepository;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.service.dto.ProjectMemberDTO;
import com.dcmc.apps.taskmanager.service.mapper.ProjectMemberMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProjectMemberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProjectMemberResourceIT {

    private static final Instant DEFAULT_ASSIGNED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/project-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberMapper projectMemberMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProjectMemberMockMvc;

    private ProjectMember projectMember;

    private ProjectMember insertedProjectMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMember createEntity(EntityManager em) {
        ProjectMember projectMember = new ProjectMember().assignedAt(DEFAULT_ASSIGNED_AT);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        projectMember.setProject(project);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        projectMember.setUser(user);
        return projectMember;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectMember createUpdatedEntity(EntityManager em) {
        ProjectMember updatedProjectMember = new ProjectMember().assignedAt(UPDATED_ASSIGNED_AT);
        // Add required entity
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            project = ProjectResourceIT.createUpdatedEntity(em);
            em.persist(project);
            em.flush();
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        updatedProjectMember.setProject(project);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedProjectMember.setUser(user);
        return updatedProjectMember;
    }

    @BeforeEach
    void initTest() {
        projectMember = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedProjectMember != null) {
            projectMemberRepository.delete(insertedProjectMember);
            insertedProjectMember = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createProjectMember() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);
        var returnedProjectMemberDTO = om.readValue(
            restProjectMemberMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(projectMemberDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProjectMemberDTO.class
        );

        // Validate the ProjectMember in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProjectMember = projectMemberMapper.toEntity(returnedProjectMemberDTO);
        assertProjectMemberUpdatableFieldsEquals(returnedProjectMember, getPersistedProjectMember(returnedProjectMember));

        insertedProjectMember = returnedProjectMember;
    }

    @Test
    @Transactional
    void createProjectMemberWithExistingId() throws Exception {
        // Create the ProjectMember with an existing ID
        projectMember.setId(1L);
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAssignedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectMember.setAssignedAt(null);

        // Create the ProjectMember, which fails.
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        restProjectMemberMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllProjectMembers() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedAt").value(hasItem(DEFAULT_ASSIGNED_AT.toString())));
    }

    @Test
    @Transactional
    void getProjectMember() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        // Get the projectMember
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, projectMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(projectMember.getId().intValue()))
            .andExpect(jsonPath("$.assignedAt").value(DEFAULT_ASSIGNED_AT.toString()));
    }

    @Test
    @Transactional
    void getProjectMembersByIdFiltering() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        Long id = projectMember.getId();

        defaultProjectMemberFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProjectMemberFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProjectMemberFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAssignedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where assignedAt equals to
        defaultProjectMemberFiltering("assignedAt.equals=" + DEFAULT_ASSIGNED_AT, "assignedAt.equals=" + UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllProjectMembersByAssignedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where assignedAt in
        defaultProjectMemberFiltering(
            "assignedAt.in=" + DEFAULT_ASSIGNED_AT + "," + UPDATED_ASSIGNED_AT,
            "assignedAt.in=" + UPDATED_ASSIGNED_AT
        );
    }

    @Test
    @Transactional
    void getAllProjectMembersByAssignedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        // Get all the projectMemberList where assignedAt is not null
        defaultProjectMemberFiltering("assignedAt.specified=true", "assignedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllProjectMembersByProjectIsEqualToSomething() throws Exception {
        Project project;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            projectMemberRepository.saveAndFlush(projectMember);
            project = ProjectResourceIT.createEntity(em);
        } else {
            project = TestUtil.findAll(em, Project.class).get(0);
        }
        em.persist(project);
        em.flush();
        projectMember.setProject(project);
        projectMemberRepository.saveAndFlush(projectMember);
        Long projectId = project.getId();
        // Get all the projectMemberList where project equals to projectId
        defaultProjectMemberShouldBeFound("projectId.equals=" + projectId);

        // Get all the projectMemberList where project equals to (projectId + 1)
        defaultProjectMemberShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    @Test
    @Transactional
    void getAllProjectMembersByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            projectMemberRepository.saveAndFlush(projectMember);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        projectMember.setUser(user);
        projectMemberRepository.saveAndFlush(projectMember);
        String userId = user.getId();
        // Get all the projectMemberList where user equals to userId
        defaultProjectMemberShouldBeFound("userId.equals=" + userId);

        // Get all the projectMemberList where user equals to "invalid-id"
        defaultProjectMemberShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    private void defaultProjectMemberFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultProjectMemberShouldBeFound(shouldBeFound);
        defaultProjectMemberShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectMemberShouldBeFound(String filter) throws Exception {
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(projectMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedAt").value(hasItem(DEFAULT_ASSIGNED_AT.toString())));

        // Check, that the count call also returns 1
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectMemberShouldNotBeFound(String filter) throws Exception {
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restProjectMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingProjectMember() throws Exception {
        // Get the projectMember
        restProjectMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProjectMember() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectMember
        ProjectMember updatedProjectMember = projectMemberRepository.findById(projectMember.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProjectMember are not directly saved in db
        em.detach(updatedProjectMember);
        updatedProjectMember.assignedAt(UPDATED_ASSIGNED_AT);
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(updatedProjectMember);

        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectMemberToMatchAllProperties(updatedProjectMember);
    }

    @Test
    @Transactional
    void putNonExistingProjectMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectMember.setId(longCount.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, projectMemberDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProjectMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectMember.setId(longCount.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProjectMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectMember.setId(longCount.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProjectMemberWithPatch() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectMember using partial update
        ProjectMember partialUpdatedProjectMember = new ProjectMember();
        partialUpdatedProjectMember.setId(projectMember.getId());

        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMember.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectMember))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectMemberUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProjectMember, projectMember),
            getPersistedProjectMember(projectMember)
        );
    }

    @Test
    @Transactional
    void fullUpdateProjectMemberWithPatch() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectMember using partial update
        ProjectMember partialUpdatedProjectMember = new ProjectMember();
        partialUpdatedProjectMember.setId(projectMember.getId());

        partialUpdatedProjectMember.assignedAt(UPDATED_ASSIGNED_AT);

        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProjectMember.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProjectMember))
            )
            .andExpect(status().isOk());

        // Validate the ProjectMember in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectMemberUpdatableFieldsEquals(partialUpdatedProjectMember, getPersistedProjectMember(partialUpdatedProjectMember));
    }

    @Test
    @Transactional
    void patchNonExistingProjectMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectMember.setId(longCount.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, projectMemberDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProjectMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectMember.setId(longCount.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProjectMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProjectMember() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectMember.setId(longCount.incrementAndGet());

        // Create the ProjectMember
        ProjectMemberDTO projectMemberDTO = projectMemberMapper.toDto(projectMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProjectMemberMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(projectMemberDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProjectMember in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProjectMember() throws Exception {
        // Initialize the database
        insertedProjectMember = projectMemberRepository.saveAndFlush(projectMember);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the projectMember
        restProjectMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, projectMember.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectMemberRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ProjectMember getPersistedProjectMember(ProjectMember projectMember) {
        return projectMemberRepository.findById(projectMember.getId()).orElseThrow();
    }

    protected void assertPersistedProjectMemberToMatchAllProperties(ProjectMember expectedProjectMember) {
        assertProjectMemberAllPropertiesEquals(expectedProjectMember, getPersistedProjectMember(expectedProjectMember));
    }

    protected void assertPersistedProjectMemberToMatchUpdatableProperties(ProjectMember expectedProjectMember) {
        assertProjectMemberAllUpdatablePropertiesEquals(expectedProjectMember, getPersistedProjectMember(expectedProjectMember));
    }
}
