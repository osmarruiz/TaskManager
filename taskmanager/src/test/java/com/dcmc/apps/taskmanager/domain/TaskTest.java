package com.dcmc.apps.taskmanager.domain;

import static com.dcmc.apps.taskmanager.domain.PriorityTestSamples.*;
import static com.dcmc.apps.taskmanager.domain.ProjectTestSamples.*;
import static com.dcmc.apps.taskmanager.domain.TaskStatusCatalogTestSamples.*;
import static com.dcmc.apps.taskmanager.domain.TaskTestSamples.*;
import static com.dcmc.apps.taskmanager.domain.WorkGroupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dcmc.apps.taskmanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = getTaskSample1();
        Task task2 = new Task();
        assertThat(task1).isNotEqualTo(task2);

        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);

        task2 = getTaskSample2();
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    void workGroupTest() {
        Task task = getTaskRandomSampleGenerator();
        WorkGroup workGroupBack = getWorkGroupRandomSampleGenerator();

        task.setWorkGroup(workGroupBack);
        assertThat(task.getWorkGroup()).isEqualTo(workGroupBack);

        task.workGroup(null);
        assertThat(task.getWorkGroup()).isNull();
    }

    @Test
    void priorityTest() {
        Task task = getTaskRandomSampleGenerator();
        Priority priorityBack = getPriorityRandomSampleGenerator();

        task.setPriority(priorityBack);
        assertThat(task.getPriority()).isEqualTo(priorityBack);

        task.priority(null);
        assertThat(task.getPriority()).isNull();
    }

    @Test
    void statusTest() {
        Task task = getTaskRandomSampleGenerator();
        TaskStatusCatalog taskStatusCatalogBack = getTaskStatusCatalogRandomSampleGenerator();

        task.setStatus(taskStatusCatalogBack);
        assertThat(task.getStatus()).isEqualTo(taskStatusCatalogBack);

        task.status(null);
        assertThat(task.getStatus()).isNull();
    }

    @Test
    void parentProjectTest() {
        Task task = getTaskRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        task.setParentProject(projectBack);
        assertThat(task.getParentProject()).isEqualTo(projectBack);

        task.parentProject(null);
        assertThat(task.getParentProject()).isNull();
    }
}
