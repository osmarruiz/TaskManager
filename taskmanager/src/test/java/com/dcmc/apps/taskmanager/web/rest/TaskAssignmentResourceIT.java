package com.dcmc.apps.taskmanager.web.rest;

import static com.dcmc.apps.taskmanager.domain.TaskAssignmentAsserts.*;
import static com.dcmc.apps.taskmanager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dcmc.apps.taskmanager.IntegrationTest;
import com.dcmc.apps.taskmanager.domain.Task;
import com.dcmc.apps.taskmanager.domain.TaskAssignment;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.repository.TaskAssignmentRepository;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.service.dto.TaskAssignmentDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskAssignmentMapper;
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
 * Integration tests for the {@link TaskAssignmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskAssignmentResourceIT {

    private static final Instant DEFAULT_ASSIGNED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ASSIGNED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/task-assignments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskAssignmentMapper taskAssignmentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskAssignmentMockMvc;

    private TaskAssignment taskAssignment;

    private TaskAssignment insertedTaskAssignment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskAssignment createEntity(EntityManager em) {
        TaskAssignment taskAssignment = new TaskAssignment().assignedAt(DEFAULT_ASSIGNED_AT);
        // Add required entity
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            task = TaskResourceIT.createEntity(em);
            em.persist(task);
            em.flush();
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        taskAssignment.setTask(task);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        taskAssignment.setUser(user);
        return taskAssignment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskAssignment createUpdatedEntity(EntityManager em) {
        TaskAssignment updatedTaskAssignment = new TaskAssignment().assignedAt(UPDATED_ASSIGNED_AT);
        // Add required entity
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            task = TaskResourceIT.createUpdatedEntity(em);
            em.persist(task);
            em.flush();
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        updatedTaskAssignment.setTask(task);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedTaskAssignment.setUser(user);
        return updatedTaskAssignment;
    }

    @BeforeEach
    void initTest() {
        taskAssignment = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTaskAssignment != null) {
            taskAssignmentRepository.delete(insertedTaskAssignment);
            insertedTaskAssignment = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createTaskAssignment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TaskAssignment
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);
        var returnedTaskAssignmentDTO = om.readValue(
            restTaskAssignmentMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(taskAssignmentDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskAssignmentDTO.class
        );

        // Validate the TaskAssignment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTaskAssignment = taskAssignmentMapper.toEntity(returnedTaskAssignmentDTO);
        assertTaskAssignmentUpdatableFieldsEquals(returnedTaskAssignment, getPersistedTaskAssignment(returnedTaskAssignment));

        insertedTaskAssignment = returnedTaskAssignment;
    }

    @Test
    @Transactional
    void createTaskAssignmentWithExistingId() throws Exception {
        // Create the TaskAssignment with an existing ID
        taskAssignment.setId(1L);
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskAssignmentMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAssignedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskAssignment.setAssignedAt(null);

        // Create the TaskAssignment, which fails.
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);

        restTaskAssignmentMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTaskAssignments() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        // Get all the taskAssignmentList
        restTaskAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedAt").value(hasItem(DEFAULT_ASSIGNED_AT.toString())));
    }

    @Test
    @Transactional
    void getTaskAssignment() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        // Get the taskAssignment
        restTaskAssignmentMockMvc
            .perform(get(ENTITY_API_URL_ID, taskAssignment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskAssignment.getId().intValue()))
            .andExpect(jsonPath("$.assignedAt").value(DEFAULT_ASSIGNED_AT.toString()));
    }

    @Test
    @Transactional
    void getTaskAssignmentsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        Long id = taskAssignment.getId();

        defaultTaskAssignmentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTaskAssignmentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTaskAssignmentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTaskAssignmentsByAssignedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        // Get all the taskAssignmentList where assignedAt equals to
        defaultTaskAssignmentFiltering("assignedAt.equals=" + DEFAULT_ASSIGNED_AT, "assignedAt.equals=" + UPDATED_ASSIGNED_AT);
    }

    @Test
    @Transactional
    void getAllTaskAssignmentsByAssignedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        // Get all the taskAssignmentList where assignedAt in
        defaultTaskAssignmentFiltering(
            "assignedAt.in=" + DEFAULT_ASSIGNED_AT + "," + UPDATED_ASSIGNED_AT,
            "assignedAt.in=" + UPDATED_ASSIGNED_AT
        );
    }

    @Test
    @Transactional
    void getAllTaskAssignmentsByAssignedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        // Get all the taskAssignmentList where assignedAt is not null
        defaultTaskAssignmentFiltering("assignedAt.specified=true", "assignedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskAssignmentsByTaskIsEqualToSomething() throws Exception {
        Task task;
        if (TestUtil.findAll(em, Task.class).isEmpty()) {
            taskAssignmentRepository.saveAndFlush(taskAssignment);
            task = TaskResourceIT.createEntity(em);
        } else {
            task = TestUtil.findAll(em, Task.class).get(0);
        }
        em.persist(task);
        em.flush();
        taskAssignment.setTask(task);
        taskAssignmentRepository.saveAndFlush(taskAssignment);
        Long taskId = task.getId();
        // Get all the taskAssignmentList where task equals to taskId
        defaultTaskAssignmentShouldBeFound("taskId.equals=" + taskId);

        // Get all the taskAssignmentList where task equals to (taskId + 1)
        defaultTaskAssignmentShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    @Test
    @Transactional
    void getAllTaskAssignmentsByUserIsEqualToSomething() throws Exception {
        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            taskAssignmentRepository.saveAndFlush(taskAssignment);
            user = UserResourceIT.createEntity();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(user);
        em.flush();
        taskAssignment.setUser(user);
        taskAssignmentRepository.saveAndFlush(taskAssignment);
        String userId = user.getId();
        // Get all the taskAssignmentList where user equals to userId
        defaultTaskAssignmentShouldBeFound("userId.equals=" + userId);

        // Get all the taskAssignmentList where user equals to "invalid-id"
        defaultTaskAssignmentShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    private void defaultTaskAssignmentFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTaskAssignmentShouldBeFound(shouldBeFound);
        defaultTaskAssignmentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskAssignmentShouldBeFound(String filter) throws Exception {
        restTaskAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskAssignment.getId().intValue())))
            .andExpect(jsonPath("$.[*].assignedAt").value(hasItem(DEFAULT_ASSIGNED_AT.toString())));

        // Check, that the count call also returns 1
        restTaskAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskAssignmentShouldNotBeFound(String filter) throws Exception {
        restTaskAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskAssignmentMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTaskAssignment() throws Exception {
        // Get the taskAssignment
        restTaskAssignmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaskAssignment() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAssignment
        TaskAssignment updatedTaskAssignment = taskAssignmentRepository.findById(taskAssignment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaskAssignment are not directly saved in db
        em.detach(updatedTaskAssignment);
        updatedTaskAssignment.assignedAt(UPDATED_ASSIGNED_AT);
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(updatedTaskAssignment);

        restTaskAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskAssignmentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the TaskAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskAssignmentToMatchAllProperties(updatedTaskAssignment);
    }

    @Test
    @Transactional
    void putNonExistingTaskAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAssignment.setId(longCount.incrementAndGet());

        // Create the TaskAssignment
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskAssignmentDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAssignment.setId(longCount.incrementAndGet());

        // Create the TaskAssignment
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAssignment.setId(longCount.incrementAndGet());

        // Create the TaskAssignment
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAssignmentMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAssignment using partial update
        TaskAssignment partialUpdatedTaskAssignment = new TaskAssignment();
        partialUpdatedTaskAssignment.setId(taskAssignment.getId());

        restTaskAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskAssignment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskAssignment))
            )
            .andExpect(status().isOk());

        // Validate the TaskAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskAssignmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTaskAssignment, taskAssignment),
            getPersistedTaskAssignment(taskAssignment)
        );
    }

    @Test
    @Transactional
    void fullUpdateTaskAssignmentWithPatch() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskAssignment using partial update
        TaskAssignment partialUpdatedTaskAssignment = new TaskAssignment();
        partialUpdatedTaskAssignment.setId(taskAssignment.getId());

        partialUpdatedTaskAssignment.assignedAt(UPDATED_ASSIGNED_AT);

        restTaskAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskAssignment.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskAssignment))
            )
            .andExpect(status().isOk());

        // Validate the TaskAssignment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskAssignmentUpdatableFieldsEquals(partialUpdatedTaskAssignment, getPersistedTaskAssignment(partialUpdatedTaskAssignment));
    }

    @Test
    @Transactional
    void patchNonExistingTaskAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAssignment.setId(longCount.incrementAndGet());

        // Create the TaskAssignment
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskAssignmentDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAssignment.setId(longCount.incrementAndGet());

        // Create the TaskAssignment
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskAssignment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskAssignment.setId(longCount.incrementAndGet());

        // Create the TaskAssignment
        TaskAssignmentDTO taskAssignmentDTO = taskAssignmentMapper.toDto(taskAssignment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskAssignmentMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskAssignmentDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskAssignment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskAssignment() throws Exception {
        // Initialize the database
        insertedTaskAssignment = taskAssignmentRepository.saveAndFlush(taskAssignment);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taskAssignment
        restTaskAssignmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskAssignment.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskAssignmentRepository.count();
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

    protected TaskAssignment getPersistedTaskAssignment(TaskAssignment taskAssignment) {
        return taskAssignmentRepository.findById(taskAssignment.getId()).orElseThrow();
    }

    protected void assertPersistedTaskAssignmentToMatchAllProperties(TaskAssignment expectedTaskAssignment) {
        assertTaskAssignmentAllPropertiesEquals(expectedTaskAssignment, getPersistedTaskAssignment(expectedTaskAssignment));
    }

    protected void assertPersistedTaskAssignmentToMatchUpdatableProperties(TaskAssignment expectedTaskAssignment) {
        assertTaskAssignmentAllUpdatablePropertiesEquals(expectedTaskAssignment, getPersistedTaskAssignment(expectedTaskAssignment));
    }
}
