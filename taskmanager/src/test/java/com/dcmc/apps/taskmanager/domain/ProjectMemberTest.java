package com.dcmc.apps.taskmanager.domain;

import static com.dcmc.apps.taskmanager.domain.ProjectMemberTestSamples.*;
import static com.dcmc.apps.taskmanager.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dcmc.apps.taskmanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectMemberTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectMember.class);
        ProjectMember projectMember1 = getProjectMemberSample1();
        ProjectMember projectMember2 = new ProjectMember();
        assertThat(projectMember1).isNotEqualTo(projectMember2);

        projectMember2.setId(projectMember1.getId());
        assertThat(projectMember1).isEqualTo(projectMember2);

        projectMember2 = getProjectMemberSample2();
        assertThat(projectMember1).isNotEqualTo(projectMember2);
    }

    @Test
    void projectTest() {
        ProjectMember projectMember = getProjectMemberRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        projectMember.setProject(projectBack);
        assertThat(projectMember.getProject()).isEqualTo(projectBack);

        projectMember.project(null);
        assertThat(projectMember.getProject()).isNull();
    }
}
