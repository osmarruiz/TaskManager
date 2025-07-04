package com.dcmc.apps.taskmanager.web.rest;

import static com.dcmc.apps.taskmanager.domain.TaskAsserts.*;
import static com.dcmc.apps.taskmanager.web.rest.TestUtil.createUpdateProxyForBean;
import static com.dcmc.apps.taskmanager.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dcmc.apps.taskmanager.IntegrationTest;
import com.dcmc.apps.taskmanager.domain.Priority;
import com.dcmc.apps.taskmanager.domain.Project;
import com.dcmc.apps.taskmanager.domain.Task;
import com.dcmc.apps.taskmanager.domain.TaskStatusCatalog;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.repository.TaskRepository;
import com.dcmc.apps.taskmanager.service.dto.TaskDTO;
import com.dcmc.apps.taskmanager.service.mapper.TaskMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final ZonedDateTime DEFAULT_DEADLINE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DEADLINE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DEADLINE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final Boolean DEFAULT_ARCHIVED = false;
    private static final Boolean UPDATED_ARCHIVED = true;

    private static final ZonedDateTime DEFAULT_ARCHIVED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_ARCHIVED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_ARCHIVED_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskMockMvc;

    private Task task;

    private Task insertedTask;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(EntityManager em) {
        Task task = new Task()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .createTime(DEFAULT_CREATE_TIME)
            .updateTime(DEFAULT_UPDATE_TIME)
            .deadline(DEFAULT_DEADLINE)
            .archived(DEFAULT_ARCHIVED)
            .archivedDate(DEFAULT_ARCHIVED_DATE);
        // Add required entity
        WorkGroup workGroup;
        if (TestUtil.findAll(em, WorkGroup.class).isEmpty()) {
            workGroup = WorkGroupResourceIT.createEntity();
            em.persist(workGroup);
            em.flush();
        } else {
            workGroup = TestUtil.findAll(em, WorkGroup.class).get(0);
        }
        task.setWorkGroup(workGroup);
        // Add required entity
        Priority priority;
        if (TestUtil.findAll(em, Priority.class).isEmpty()) {
            priority = PriorityResourceIT.createEntity();
            em.persist(priority);
            em.flush();
        } else {
            priority = TestUtil.findAll(em, Priority.class).get(0);
        }
        task.setPriority(priority);
        // Add required entity
        TaskStatusCatalog taskStatusCatalog;
        if (TestUtil.findAll(em, TaskStatusCatalog.class).isEmpty()) {
            taskStatusCatalog = TaskStatusCatalogResourceIT.createEntity(em);
            em.persist(taskStatusCatalog);
            em.flush();
        } else {
            taskStatusCatalog = TestUtil.findAll(em, TaskStatusCatalog.class).get(0);
        }
        task.setStatus(taskStatusCatalog);
        return task;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity(EntityManager em) {
        Task updatedTask = new Task()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME)
            .deadline(UPDATED_DEADLINE)
            .archived(UPDATED_ARCHIVED)
            .archivedDate(UPDATED_ARCHIVED_DATE);
        // Add required entity
        WorkGroup workGroup;
        if (TestUtil.findAll(em, WorkGroup.class).isEmpty()) {
            workGroup = WorkGroupResourceIT.createUpdatedEntity();
            em.persist(workGroup);
            em.flush();
        } else {
            workGroup = TestUtil.findAll(em, WorkGroup.class).get(0);
        }
        updatedTask.setWorkGroup(workGroup);
        // Add required entity
        Priority priority;
        if (TestUtil.findAll(em, Priority.class).isEmpty()) {
            priority = PriorityResourceIT.createUpdatedEntity();
            em.persist(priority);
            em.flush();
        } else {
            priority = TestUtil.findAll(em, Priority.class).get(0);
        }
        updatedTask.setPriority(priority);
        // Add required entity
        TaskStatusCatalog taskStatusCatalog;
        if (TestUtil.findAll(em, TaskStatusCatalog.class).isEmpty()) {
            taskStatusCatalog = TaskStatusCatalogResourceIT.createUpdatedEntity(em);
            em.persist(taskStatusCatalog);
            em.flush();
        } else {
            taskStatusCatalog = TestUtil.findAll(em, TaskStatusCatalog.class).get(0);
        }
        updatedTask.setStatus(taskStatusCatalog);
        return updatedTask;
    }

    @BeforeEach
    void initTest() {
        task = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedTask != null) {
            taskRepository.delete(insertedTask);
            insertedTask = null;
        }
    }

    @Test
    @Transactional
    void createTask() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        var returnedTaskDTO = om.readValue(
            restTaskMockMvc
                .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TaskDTO.class
        );

        // Validate the Task in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTask = taskMapper.toEntity(returnedTaskDTO);
        assertTaskUpdatableFieldsEquals(returnedTask, getPersistedTask(returnedTask));

        insertedTask = returnedTask;
    }

    @Test
    @Transactional
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId(1L);
        TaskDTO taskDTO = taskMapper.toDto(task);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        task.setTitle(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        task.setDescription(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        task.setCreateTime(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdateTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        task.setUpdateTime(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTasks() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(DEFAULT_UPDATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(sameInstant(DEFAULT_DEADLINE))))
            .andExpect(jsonPath("$.[*].archived").value(hasItem(DEFAULT_ARCHIVED)))
            .andExpect(jsonPath("$.[*].archivedDate").value(hasItem(sameInstant(DEFAULT_ARCHIVED_DATE))));
    }

    @Test
    @Transactional
    void getTask() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.createTime").value(DEFAULT_CREATE_TIME.toString()))
            .andExpect(jsonPath("$.updateTime").value(DEFAULT_UPDATE_TIME.toString()))
            .andExpect(jsonPath("$.deadline").value(sameInstant(DEFAULT_DEADLINE)))
            .andExpect(jsonPath("$.archived").value(DEFAULT_ARCHIVED))
            .andExpect(jsonPath("$.archivedDate").value(sameInstant(DEFAULT_ARCHIVED_DATE)));
    }

    @Test
    @Transactional
    void getTasksByIdFiltering() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        Long id = task.getId();

        defaultTaskFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTaskFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTaskFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTasksByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where title equals to
        defaultTaskFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTasksByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where title in
        defaultTaskFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTasksByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where title is not null
        defaultTaskFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByTitleContainsSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where title contains
        defaultTaskFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTasksByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where title does not contain
        defaultTaskFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where description equals to
        defaultTaskFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where description in
        defaultTaskFiltering("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION, "description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where description is not null
        defaultTaskFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where description contains
        defaultTaskFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where description does not contain
        defaultTaskFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTasksByCreateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where createTime equals to
        defaultTaskFiltering("createTime.equals=" + DEFAULT_CREATE_TIME, "createTime.equals=" + UPDATED_CREATE_TIME);
    }

    @Test
    @Transactional
    void getAllTasksByCreateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where createTime in
        defaultTaskFiltering("createTime.in=" + DEFAULT_CREATE_TIME + "," + UPDATED_CREATE_TIME, "createTime.in=" + UPDATED_CREATE_TIME);
    }

    @Test
    @Transactional
    void getAllTasksByCreateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where createTime is not null
        defaultTaskFiltering("createTime.specified=true", "createTime.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByUpdateTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where updateTime equals to
        defaultTaskFiltering("updateTime.equals=" + DEFAULT_UPDATE_TIME, "updateTime.equals=" + UPDATED_UPDATE_TIME);
    }

    @Test
    @Transactional
    void getAllTasksByUpdateTimeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where updateTime in
        defaultTaskFiltering("updateTime.in=" + DEFAULT_UPDATE_TIME + "," + UPDATED_UPDATE_TIME, "updateTime.in=" + UPDATED_UPDATE_TIME);
    }

    @Test
    @Transactional
    void getAllTasksByUpdateTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where updateTime is not null
        defaultTaskFiltering("updateTime.specified=true", "updateTime.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDeadlineIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where deadline equals to
        defaultTaskFiltering("deadline.equals=" + DEFAULT_DEADLINE, "deadline.equals=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllTasksByDeadlineIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where deadline in
        defaultTaskFiltering("deadline.in=" + DEFAULT_DEADLINE + "," + UPDATED_DEADLINE, "deadline.in=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllTasksByDeadlineIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where deadline is not null
        defaultTaskFiltering("deadline.specified=true", "deadline.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDeadlineIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where deadline is greater than or equal to
        defaultTaskFiltering("deadline.greaterThanOrEqual=" + DEFAULT_DEADLINE, "deadline.greaterThanOrEqual=" + UPDATED_DEADLINE);
    }

    @Test
    @Transactional
    void getAllTasksByDeadlineIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where deadline is less than or equal to
        defaultTaskFiltering("deadline.lessThanOrEqual=" + DEFAULT_DEADLINE, "deadline.lessThanOrEqual=" + SMALLER_DEADLINE);
    }

    @Test
    @Transactional
    void getAllTasksByDeadlineIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where deadline is less than
        defaultTaskFiltering("deadline.lessThan=" + UPDATED_DEADLINE, "deadline.lessThan=" + DEFAULT_DEADLINE);
    }

    @Test
    @Transactional
    void getAllTasksByDeadlineIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where deadline is greater than
        defaultTaskFiltering("deadline.greaterThan=" + SMALLER_DEADLINE, "deadline.greaterThan=" + DEFAULT_DEADLINE);
    }

    @Test
    @Transactional
    void getAllTasksByArchivedIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archived equals to
        defaultTaskFiltering("archived.equals=" + DEFAULT_ARCHIVED, "archived.equals=" + UPDATED_ARCHIVED);
    }

    @Test
    @Transactional
    void getAllTasksByArchivedIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archived in
        defaultTaskFiltering("archived.in=" + DEFAULT_ARCHIVED + "," + UPDATED_ARCHIVED, "archived.in=" + UPDATED_ARCHIVED);
    }

    @Test
    @Transactional
    void getAllTasksByArchivedIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archived is not null
        defaultTaskFiltering("archived.specified=true", "archived.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByArchivedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archivedDate equals to
        defaultTaskFiltering("archivedDate.equals=" + DEFAULT_ARCHIVED_DATE, "archivedDate.equals=" + UPDATED_ARCHIVED_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByArchivedDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archivedDate in
        defaultTaskFiltering(
            "archivedDate.in=" + DEFAULT_ARCHIVED_DATE + "," + UPDATED_ARCHIVED_DATE,
            "archivedDate.in=" + UPDATED_ARCHIVED_DATE
        );
    }

    @Test
    @Transactional
    void getAllTasksByArchivedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archivedDate is not null
        defaultTaskFiltering("archivedDate.specified=true", "archivedDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByArchivedDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archivedDate is greater than or equal to
        defaultTaskFiltering(
            "archivedDate.greaterThanOrEqual=" + DEFAULT_ARCHIVED_DATE,
            "archivedDate.greaterThanOrEqual=" + UPDATED_ARCHIVED_DATE
        );
    }

    @Test
    @Transactional
    void getAllTasksByArchivedDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archivedDate is less than or equal to
        defaultTaskFiltering(
            "archivedDate.lessThanOrEqual=" + DEFAULT_ARCHIVED_DATE,
            "archivedDate.lessThanOrEqual=" + SMALLER_ARCHIVED_DATE
        );
    }

    @Test
    @Transactional
    void getAllTasksByArchivedDateIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archivedDate is less than
        defaultTaskFiltering("archivedDate.lessThan=" + UPDATED_ARCHIVED_DATE, "archivedDate.lessThan=" + DEFAULT_ARCHIVED_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByArchivedDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        // Get all the taskList where archivedDate is greater than
        defaultTaskFiltering("archivedDate.greaterThan=" + SMALLER_ARCHIVED_DATE, "archivedDate.greaterThan=" + DEFAULT_ARCHIVED_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByWorkGroupIsEqualToSomething() throws Exception {
        WorkGroup workGroup;
        if (TestUtil.findAll(em, WorkGroup.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            workGroup = WorkGroupResourceIT.createEntity();
        } else {
            workGroup = TestUtil.findAll(em, WorkGroup.class).get(0);
        }
        em.persist(workGroup);
        em.flush();
        task.setWorkGroup(workGroup);
        taskRepository.saveAndFlush(task);
        Long workGroupId = workGroup.getId();
        // Get all the taskList where workGroup equals to workGroupId
        defaultTaskShouldBeFound("workGroupId.equals=" + workGroupId);

        // Get all the taskList where workGroup equals to (workGroupId + 1)
        defaultTaskShouldNotBeFound("workGroupId.equals=" + (workGroupId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByPriorityIsEqualToSomething() throws Exception {
        Priority priority;
        if (TestUtil.findAll(em, Priority.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            priority = PriorityResourceIT.createEntity();
        } else {
            priority = TestUtil.findAll(em, Priority.class).get(0);
        }
        em.persist(priority);
        em.flush();
        task.setPriority(priority);
        taskRepository.saveAndFlush(task);
        Long priorityId = priority.getId();
        // Get all the taskList where priority equals to priorityId
        defaultTaskShouldBeFound("priorityId.equals=" + priorityId);

        // Get all the taskList where priority equals to (priorityId + 1)
        defaultTaskShouldNotBeFound("priorityId.equals=" + (priorityId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByStatusIsEqualToSomething() throws Exception {
        TaskStatusCatalog status;
        if (TestUtil.findAll(em, TaskStatusCatalog.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            status = TaskStatusCatalogResourceIT.createEntity(em);
        } else {
            status = TestUtil.findAll(em, TaskStatusCatalog.class).get(0);
        }
        em.persist(status);
        em.flush();
        task.setStatus(status);
        taskRepository.saveAndFlush(task);
        Long statusId = status.getId();
        // Get all the taskList where status equals to statusId
        defaultTaskShouldBeFound("statusId.equals=" + statusId);

        // Get all the taskList where status equals to (statusId + 1)
        defaultTaskShouldNotBeFound("statusId.equals=" + (statusId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByParentProjectIsEqualToSomething() throws Exception {
        Project parentProject;
        if (TestUtil.findAll(em, Project.class).isEmpty()) {
            taskRepository.saveAndFlush(task);
            parentProject = ProjectResourceIT.createEntity(em);
        } else {
            parentProject = TestUtil.findAll(em, Project.class).get(0);
        }
        em.persist(parentProject);
        em.flush();
        task.setParentProject(parentProject);
        taskRepository.saveAndFlush(task);
        Long parentProjectId = parentProject.getId();
        // Get all the taskList where parentProject equals to parentProjectId
        defaultTaskShouldBeFound("parentProjectId.equals=" + parentProjectId);

        // Get all the taskList where parentProject equals to (parentProjectId + 1)
        defaultTaskShouldNotBeFound("parentProjectId.equals=" + (parentProjectId + 1));
    }

    private void defaultTaskFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTaskShouldBeFound(shouldBeFound);
        defaultTaskShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskShouldBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].createTime").value(hasItem(DEFAULT_CREATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].updateTime").value(hasItem(DEFAULT_UPDATE_TIME.toString())))
            .andExpect(jsonPath("$.[*].deadline").value(hasItem(sameInstant(DEFAULT_DEADLINE))))
            .andExpect(jsonPath("$.[*].archived").value(hasItem(DEFAULT_ARCHIVED)))
            .andExpect(jsonPath("$.[*].archivedDate").value(hasItem(sameInstant(DEFAULT_ARCHIVED_DATE))));

        // Check, that the count call also returns 1
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskShouldNotBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTask() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME)
            .deadline(UPDATED_DEADLINE)
            .archived(UPDATED_ARCHIVED)
            .archivedDate(UPDATED_ARCHIVED_DATE);
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskDTO))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTaskToMatchAllProperties(updatedTask);
    }

    @Test
    @Transactional
    void putNonExistingTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(taskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createTime(UPDATED_CREATE_TIME)
            .deadline(UPDATED_DEADLINE)
            .archived(UPDATED_ARCHIVED);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTask, task), getPersistedTask(task));
    }

    @Test
    @Transactional
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .createTime(UPDATED_CREATE_TIME)
            .updateTime(UPDATED_UPDATE_TIME)
            .deadline(UPDATED_DEADLINE)
            .archived(UPDATED_ARCHIVED)
            .archivedDate(UPDATED_ARCHIVED_DATE);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTaskUpdatableFieldsEquals(partialUpdatedTask, getPersistedTask(partialUpdatedTask));
    }

    @Test
    @Transactional
    void patchNonExistingTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTask() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        task.setId(longCount.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(taskDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTask() throws Exception {
        // Initialize the database
        insertedTask = taskRepository.saveAndFlush(task);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the task
        restTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, task.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return taskRepository.count();
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

    protected Task getPersistedTask(Task task) {
        return taskRepository.findById(task.getId()).orElseThrow();
    }

    protected void assertPersistedTaskToMatchAllProperties(Task expectedTask) {
        assertTaskAllPropertiesEquals(expectedTask, getPersistedTask(expectedTask));
    }

    protected void assertPersistedTaskToMatchUpdatableProperties(Task expectedTask) {
        assertTaskAllUpdatablePropertiesEquals(expectedTask, getPersistedTask(expectedTask));
    }
}
