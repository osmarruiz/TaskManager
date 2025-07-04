package com.dcmc.apps.taskmanager.service.mapper;

import static com.dcmc.apps.taskmanager.domain.ProjectMemberAsserts.*;
import static com.dcmc.apps.taskmanager.domain.ProjectMemberTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectMemberMapperTest {

    private ProjectMemberMapper projectMemberMapper;

    @BeforeEach
    void setUp() {
        projectMemberMapper = new ProjectMemberMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProjectMemberSample1();
        var actual = projectMemberMapper.toEntity(projectMemberMapper.toDto(expected));
        assertProjectMemberAllPropertiesEquals(expected, actual);
    }
}
