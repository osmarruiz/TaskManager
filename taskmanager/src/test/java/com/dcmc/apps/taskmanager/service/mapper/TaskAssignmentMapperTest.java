package com.dcmc.apps.taskmanager.service.mapper;

import static com.dcmc.apps.taskmanager.domain.TaskAssignmentAsserts.*;
import static com.dcmc.apps.taskmanager.domain.TaskAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskAssignmentMapperTest {

    private TaskAssignmentMapper taskAssignmentMapper;

    @BeforeEach
    void setUp() {
        taskAssignmentMapper = new TaskAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTaskAssignmentSample1();
        var actual = taskAssignmentMapper.toEntity(taskAssignmentMapper.toDto(expected));
        assertTaskAssignmentAllPropertiesEquals(expected, actual);
    }
}
