package com.dcmc.apps.taskmanager.web.rest;

import static com.dcmc.apps.taskmanager.domain.WorkGroupAsserts.*;
import static com.dcmc.apps.taskmanager.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dcmc.apps.taskmanager.IntegrationTest;
import com.dcmc.apps.taskmanager.domain.WorkGroup;
import com.dcmc.apps.taskmanager.repository.WorkGroupRepository;
import com.dcmc.apps.taskmanager.service.dto.WorkGroupDTO;
import com.dcmc.apps.taskmanager.service.mapper.WorkGroupMapper;
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
 * Integration tests for the {@link WorkGroupResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WorkGroupResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/work-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private WorkGroupRepository workGroupRepository;

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWorkGroupMockMvc;

    private WorkGroup workGroup;

    private WorkGroup insertedWorkGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkGroup createEntity() {
        return new WorkGroup().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).creationDate(DEFAULT_CREATION_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WorkGroup createUpdatedEntity() {
        return new WorkGroup().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).creationDate(UPDATED_CREATION_DATE);
    }

    @BeforeEach
    void initTest() {
        workGroup = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedWorkGroup != null) {
            workGroupRepository.delete(insertedWorkGroup);
            insertedWorkGroup = null;
        }
    }

    @Test
    @Transactional
    void createWorkGroup() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);
        var returnedWorkGroupDTO = om.readValue(
            restWorkGroupMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workGroupDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            WorkGroupDTO.class
        );

        // Validate the WorkGroup in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedWorkGroup = workGroupMapper.toEntity(returnedWorkGroupDTO);
        assertWorkGroupUpdatableFieldsEquals(returnedWorkGroup, getPersistedWorkGroup(returnedWorkGroup));

        insertedWorkGroup = returnedWorkGroup;
    }

    @Test
    @Transactional
    void createWorkGroupWithExistingId() throws Exception {
        // Create the WorkGroup with an existing ID
        workGroup.setId(1L);
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWorkGroupMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workGroup.setName(null);

        // Create the WorkGroup, which fails.
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        restWorkGroupMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workGroupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        workGroup.setCreationDate(null);

        // Create the WorkGroup, which fails.
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        restWorkGroupMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workGroupDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWorkGroups() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));
    }

    @Test
    @Transactional
    void getWorkGroup() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get the workGroup
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL_ID, workGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(workGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()));
    }

    @Test
    @Transactional
    void getWorkGroupsByIdFiltering() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        Long id = workGroup.getId();

        defaultWorkGroupFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultWorkGroupFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultWorkGroupFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name equals to
        defaultWorkGroupFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name in
        defaultWorkGroupFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name is not null
        defaultWorkGroupFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name contains
        defaultWorkGroupFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where name does not contain
        defaultWorkGroupFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where description equals to
        defaultWorkGroupFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where description in
        defaultWorkGroupFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    @Transactional
    void getAllWorkGroupsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where description is not null
        defaultWorkGroupFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    @Transactional
    void getAllWorkGroupsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where description contains
        defaultWorkGroupFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where description does not contain
        defaultWorkGroupFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByCreationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where creationDate equals to
        defaultWorkGroupFiltering("creationDate.equals=" + DEFAULT_CREATION_DATE, "creationDate.equals=" + UPDATED_CREATION_DATE);
    }

    @Test
    @Transactional
    void getAllWorkGroupsByCreationDateIsInShouldWork() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where creationDate in
        defaultWorkGroupFiltering(
            "creationDate.in=" + DEFAULT_CREATION_DATE + "," + UPDATED_CREATION_DATE,
            "creationDate.in=" + UPDATED_CREATION_DATE
        );
    }

    @Test
    @Transactional
    void getAllWorkGroupsByCreationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        // Get all the workGroupList where creationDate is not null
        defaultWorkGroupFiltering("creationDate.specified=true", "creationDate.specified=false");
    }

    private void defaultWorkGroupFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultWorkGroupShouldBeFound(shouldBeFound);
        defaultWorkGroupShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWorkGroupShouldBeFound(String filter) throws Exception {
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(workGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())));

        // Check, that the count call also returns 1
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWorkGroupShouldNotBeFound(String filter) throws Exception {
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWorkGroupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWorkGroup() throws Exception {
        // Get the workGroup
        restWorkGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingWorkGroup() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workGroup
        WorkGroup updatedWorkGroup = workGroupRepository.findById(workGroup.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedWorkGroup are not directly saved in db
        em.detach(updatedWorkGroup);
        updatedWorkGroup.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).creationDate(UPDATED_CREATION_DATE);
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(updatedWorkGroup);

        restWorkGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workGroupDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(workGroupDTO))
            )
            .andExpect(status().isOk());

        // Validate the WorkGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedWorkGroupToMatchAllProperties(updatedWorkGroup);
    }

    @Test
    @Transactional
    void putNonExistingWorkGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workGroup.setId(longCount.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, workGroupDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(workGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWorkGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workGroup.setId(longCount.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(workGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWorkGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workGroup.setId(longCount.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(workGroupDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWorkGroupWithPatch() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workGroup using partial update
        WorkGroup partialUpdatedWorkGroup = new WorkGroup();
        partialUpdatedWorkGroup.setId(workGroup.getId());

        partialUpdatedWorkGroup.name(UPDATED_NAME).creationDate(UPDATED_CREATION_DATE);

        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkGroup.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWorkGroup))
            )
            .andExpect(status().isOk());

        // Validate the WorkGroup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkGroupUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedWorkGroup, workGroup),
            getPersistedWorkGroup(workGroup)
        );
    }

    @Test
    @Transactional
    void fullUpdateWorkGroupWithPatch() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the workGroup using partial update
        WorkGroup partialUpdatedWorkGroup = new WorkGroup();
        partialUpdatedWorkGroup.setId(workGroup.getId());

        partialUpdatedWorkGroup.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).creationDate(UPDATED_CREATION_DATE);

        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWorkGroup.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedWorkGroup))
            )
            .andExpect(status().isOk());

        // Validate the WorkGroup in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertWorkGroupUpdatableFieldsEquals(partialUpdatedWorkGroup, getPersistedWorkGroup(partialUpdatedWorkGroup));
    }

    @Test
    @Transactional
    void patchNonExistingWorkGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workGroup.setId(longCount.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, workGroupDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(workGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWorkGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workGroup.setId(longCount.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(workGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WorkGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWorkGroup() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        workGroup.setId(longCount.incrementAndGet());

        // Create the WorkGroup
        WorkGroupDTO workGroupDTO = workGroupMapper.toDto(workGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWorkGroupMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(workGroupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WorkGroup in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWorkGroup() throws Exception {
        // Initialize the database
        insertedWorkGroup = workGroupRepository.saveAndFlush(workGroup);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the workGroup
        restWorkGroupMockMvc
            .perform(delete(ENTITY_API_URL_ID, workGroup.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return workGroupRepository.count();
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

    protected WorkGroup getPersistedWorkGroup(WorkGroup workGroup) {
        return workGroupRepository.findById(workGroup.getId()).orElseThrow();
    }

    protected void assertPersistedWorkGroupToMatchAllProperties(WorkGroup expectedWorkGroup) {
        assertWorkGroupAllPropertiesEquals(expectedWorkGroup, getPersistedWorkGroup(expectedWorkGroup));
    }

    protected void assertPersistedWorkGroupToMatchUpdatableProperties(WorkGroup expectedWorkGroup) {
        assertWorkGroupAllUpdatablePropertiesEquals(expectedWorkGroup, getPersistedWorkGroup(expectedWorkGroup));
    }
}
