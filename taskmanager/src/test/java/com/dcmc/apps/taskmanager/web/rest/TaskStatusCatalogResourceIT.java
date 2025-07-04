package com.dcmc.apps.taskmanager.web.rest;

import static com.dcmc.apps.taskmanager.domain.TaskStatusCatalogAsserts.*;
import static com.dcmc.apps.taskmanager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dcmc.apps.taskmanager.IntegrationTest;
import com.dcmc.apps.taskmanager.domain.TaskStatusCatalog;
import com.dcmc.apps.taskmanager.domain.User;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.repository.TaskStatusCatalogRepository;
import com.dcmc.apps.taskmanager.repository.UserRepository;
import com.dcmc.apps.taskmanager.service.dto.TaskStatusCatalogDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskStatusCatalogMapper;
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
 * Integration tests for the {@link TaskStatusCatalogResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskStatusCatalogResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/task-status-catalogs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusCatalogRepository taskStatusCatalogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusCatalogMapper taskStatusCatalogMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskStatusCatalogMockMvc;

    private TaskStatusCatalog taskStatusCatalog;

    private TaskStatusCatalog insertedTaskStatusCatalog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskStatusCatalog createEntity(EntityManager em) {
        TaskStatusCatalog taskStatusCatalog = new TaskStatusCatalog()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT);

        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        taskStatusCatalog.setCreatedBy(user);
        return taskStatusCatalog;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskStatusCatalog createUpdatedEntity(EntityManager em) {
        TaskStatusCatalog updatedTaskStatusCatalog = new TaskStatusCatalog()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedTaskStatusCatalog.setCreatedBy(user);
        return updatedTaskStatusCatalog;
    }

    @BeforeEach
    void initTest() {
        taskStatusCatalog = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTaskStatusCatalog != null) {
            taskStatusCatalogRepository.delete(insertedTaskStatusCatalog);
            insertedTaskStatusCatalog = null;
        }
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void createTaskStatusCatalog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TaskStatusCatalog
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);
        var returnedTaskStatusCatalogDTO = om.readValue(
            restTaskStatusCatalogMockMvc
                .perform(
                    post(ENTITY_API_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(taskStatusCatalogDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskStatusCatalogDTO.class
        );

        // Validate the TaskStatusCatalog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTaskStatusCatalog = taskStatusCatalogMapper.toEntity(returnedTaskStatusCatalogDTO);
        assertTaskStatusCatalogUpdatableFieldsEquals(returnedTaskStatusCatalog, getPersistedTaskStatusCatalog(returnedTaskStatusCatalog));

        insertedTaskStatusCatalog = returnedTaskStatusCatalog;
    }

    @Test
    @Transactional
    void createTaskStatusCatalogWithExistingId() throws Exception {
        // Create the TaskStatusCatalog with an existing ID
        taskStatusCatalog.setId(1L);
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskStatusCatalogMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskStatusCatalog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskStatusCatalog.setName(null);

        // Create the TaskStatusCatalog, which fails.
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        restTaskStatusCatalogMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        taskStatusCatalog.setCreatedAt(null);

        // Create the TaskStatusCatalog, which fails.
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        restTaskStatusCatalogMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogs() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList
        restTaskStatusCatalogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskStatusCatalog.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @Test
    @Transactional
    void getTaskStatusCatalog() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get the taskStatusCatalog
        restTaskStatusCatalogMockMvc
            .perform(get(ENTITY_API_URL_ID, taskStatusCatalog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskStatusCatalog.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getTaskStatusCatalogsByIdFiltering() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        Long id = taskStatusCatalog.getId();

        defaultTaskStatusCatalogFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTaskStatusCatalogFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTaskStatusCatalogFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where name equals to
        defaultTaskStatusCatalogFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where name in
        defaultTaskStatusCatalogFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where name is not null
        defaultTaskStatusCatalogFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where name contains
        defaultTaskStatusCatalogFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where name does not contain
        defaultTaskStatusCatalogFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where description equals to
        defaultTaskStatusCatalogFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where description in
        defaultTaskStatusCatalogFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where description is not null
        defaultTaskStatusCatalogFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where description contains
        defaultTaskStatusCatalogFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where description does not contain
        defaultTaskStatusCatalogFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where createdAt equals to
        defaultTaskStatusCatalogFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where createdAt in
        defaultTaskStatusCatalogFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where createdAt is not null
        defaultTaskStatusCatalogFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where updatedAt equals to
        defaultTaskStatusCatalogFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where updatedAt in
        defaultTaskStatusCatalogFiltering(
            "updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT,
            "updatedAt.in=" + UPDATED_UPDATED_AT
        );
    }

    @Test
    @Transactional
    void getAllTaskStatusCatalogsByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        // Get all the taskStatusCatalogList where updatedAt is not null
        defaultTaskStatusCatalogFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }


    @Test
    @Transactional
    void getAllTaskStatusCatalogsByCreatedByIsEqualToSomething() throws Exception {
        User createdBy;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);
            createdBy = UserResourceIT.createEntity();
        } else {
            createdBy = TestUtil.findAll(em, User.class).get(0);
        }
        em.persist(createdBy);
        em.flush();
        taskStatusCatalog.setCreatedBy(createdBy);
        taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);
        String createdById = createdBy.getId();
        // Get all the taskStatusCatalogList where createdBy equals to createdById
        defaultTaskStatusCatalogShouldBeFound("createdById.equals=" + createdById);

        // Get all the taskStatusCatalogList where createdBy equals to "invalid-id"
        defaultTaskStatusCatalogShouldNotBeFound("createdById.equals=" + "invalid-id");
    }

    private void defaultTaskStatusCatalogFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTaskStatusCatalogShouldBeFound(shouldBeFound);
        defaultTaskStatusCatalogShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskStatusCatalogShouldBeFound(String filter) throws Exception {
        restTaskStatusCatalogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskStatusCatalog.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));

        // Check, that the count call also returns 1
        restTaskStatusCatalogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskStatusCatalogShouldNotBeFound(String filter) throws Exception {
        restTaskStatusCatalogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskStatusCatalogMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTaskStatusCatalog() throws Exception {
        // Get the taskStatusCatalog
        restTaskStatusCatalogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTaskStatusCatalog() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskStatusCatalog
        TaskStatusCatalog updatedTaskStatusCatalog = taskStatusCatalogRepository.findById(taskStatusCatalog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTaskStatusCatalog are not directly saved in db
        em.detach(updatedTaskStatusCatalog);
        updatedTaskStatusCatalog
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(updatedTaskStatusCatalog);

        restTaskStatusCatalogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskStatusCatalogDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isOk());

        // Validate the TaskStatusCatalog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskStatusCatalogToMatchAllProperties(updatedTaskStatusCatalog);
    }

    @Test
    @Transactional
    void putNonExistingTaskStatusCatalog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskStatusCatalog.setId(longCount.incrementAndGet());

        // Create the TaskStatusCatalog
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskStatusCatalogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskStatusCatalogDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskStatusCatalog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskStatusCatalog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskStatusCatalog.setId(longCount.incrementAndGet());

        // Create the TaskStatusCatalog
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskStatusCatalogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskStatusCatalog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskStatusCatalog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskStatusCatalog.setId(longCount.incrementAndGet());

        // Create the TaskStatusCatalog
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskStatusCatalogMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskStatusCatalog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskStatusCatalogWithPatch() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskStatusCatalog using partial update
        TaskStatusCatalog partialUpdatedTaskStatusCatalog = new TaskStatusCatalog();
        partialUpdatedTaskStatusCatalog.setId(taskStatusCatalog.getId());

        partialUpdatedTaskStatusCatalog.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restTaskStatusCatalogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskStatusCatalog.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskStatusCatalog))
            )
            .andExpect(status().isOk());

        // Validate the TaskStatusCatalog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskStatusCatalogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTaskStatusCatalog, taskStatusCatalog),
            getPersistedTaskStatusCatalog(taskStatusCatalog)
        );
    }

    @Test
    @Transactional
    void fullUpdateTaskStatusCatalogWithPatch() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the taskStatusCatalog using partial update
        TaskStatusCatalog partialUpdatedTaskStatusCatalog = new TaskStatusCatalog();
        partialUpdatedTaskStatusCatalog.setId(taskStatusCatalog.getId());

        partialUpdatedTaskStatusCatalog
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT);

        restTaskStatusCatalogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskStatusCatalog.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTaskStatusCatalog))
            )
            .andExpect(status().isOk());

        // Validate the TaskStatusCatalog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskStatusCatalogUpdatableFieldsEquals(
            partialUpdatedTaskStatusCatalog,
            getPersistedTaskStatusCatalog(partialUpdatedTaskStatusCatalog)
        );
    }

    @Test
    @Transactional
    void patchNonExistingTaskStatusCatalog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskStatusCatalog.setId(longCount.incrementAndGet());

        // Create the TaskStatusCatalog
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskStatusCatalogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskStatusCatalogDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskStatusCatalog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskStatusCatalog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskStatusCatalog.setId(longCount.incrementAndGet());

        // Create the TaskStatusCatalog
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskStatusCatalogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskStatusCatalog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskStatusCatalog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        taskStatusCatalog.setId(longCount.incrementAndGet());

        // Create the TaskStatusCatalog
        TaskStatusCatalogDTO taskStatusCatalogDTO = taskStatusCatalogMapper.toDto(taskStatusCatalog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskStatusCatalogMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskStatusCatalogDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskStatusCatalog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskStatusCatalog() throws Exception {
        // Initialize the database
        insertedTaskStatusCatalog = taskStatusCatalogRepository.saveAndFlush(taskStatusCatalog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the taskStatusCatalog
        restTaskStatusCatalogMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskStatusCatalog.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskStatusCatalogRepository.count();
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

    protected TaskStatusCatalog getPersistedTaskStatusCatalog(TaskStatusCatalog taskStatusCatalog) {
        return taskStatusCatalogRepository.findById(taskStatusCatalog.getId()).orElseThrow();
    }

    protected void assertPersistedTaskStatusCatalogToMatchAllProperties(TaskStatusCatalog expectedTaskStatusCatalog) {
        assertTaskStatusCatalogAllPropertiesEquals(expectedTaskStatusCatalog, getPersistedTaskStatusCatalog(expectedTaskStatusCatalog));
    }

    protected void assertPersistedTaskStatusCatalogToMatchUpdatableProperties(TaskStatusCatalog expectedTaskStatusCatalog) {
        assertTaskStatusCatalogAllUpdatablePropertiesEquals(
            expectedTaskStatusCatalog,
            getPersistedTaskStatusCatalog(expectedTaskStatusCatalog)
        );
    }
}
