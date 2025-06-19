package com.dcmc.apps.taskmanager.service.mapper;

import static com.dcmc.apps.taskmanager.domain.TaskStatusCatalogAsserts.*;
import static com.dcmc.apps.taskmanager.domain.TaskStatusCatalogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskStatusCatalogMapperTest {

    private TaskStatusCatalogMapper taskStatusCatalogMapper;

    @BeforeEach
    void setUp() {
        taskStatusCatalogMapper = new TaskStatusCatalogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTaskStatusCatalogSample1();
        var actual = taskStatusCatalogMapper.toEntity(taskStatusCatalogMapper.toDto(expected));
        assertTaskStatusCatalogAllPropertiesEquals(expected, actual);
    }
}
