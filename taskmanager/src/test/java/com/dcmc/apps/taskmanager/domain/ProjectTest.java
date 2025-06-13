package com.dcmc.apps.taskmanager.domain;

import static com.dcmc.apps.taskmanager.domain.ProjectTestSamples.*;
import static com.dcmc.apps.taskmanager.domain.WorkGroupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dcmc.apps.taskmanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Project.class);
        Project project1 = getProjectSample1();
        Project project2 = new Project();
        assertThat(project1).isNotEqualTo(project2);

        project2.setId(project1.getId());
        assertThat(project1).isEqualTo(project2);

        project2 = getProjectSample2();
        assertThat(project1).isNotEqualTo(project2);
    }

    @Test
    void workGroupTest() {
        Project project = getProjectRandomSampleGenerator();
        WorkGroup workGroupBack = getWorkGroupRandomSampleGenerator();

        project.setWorkGroup(workGroupBack);
        assertThat(project.getWorkGroup()).isEqualTo(workGroupBack);

        project.workGroup(null);
        assertThat(project.getWorkGroup()).isNull();
    }
}
