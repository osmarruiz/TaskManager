package com.dcmc.apps.taskmanager.domain;

import static com.dcmc.apps.taskmanager.domain.TaskAssignmentTestSamples.*;
import static com.dcmc.apps.taskmanager.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dcmc.apps.taskmanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskAssignmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskAssignment.class);
        TaskAssignment taskAssignment1 = getTaskAssignmentSample1();
        TaskAssignment taskAssignment2 = new TaskAssignment();
        assertThat(taskAssignment1).isNotEqualTo(taskAssignment2);

        taskAssignment2.setId(taskAssignment1.getId());
        assertThat(taskAssignment1).isEqualTo(taskAssignment2);

        taskAssignment2 = getTaskAssignmentSample2();
        assertThat(taskAssignment1).isNotEqualTo(taskAssignment2);
    }

    @Test
    void taskTest() {
        TaskAssignment taskAssignment = getTaskAssignmentRandomSampleGenerator();
        Task taskBack = getTaskRandomSampleGenerator();

        taskAssignment.setTask(taskBack);
        assertThat(taskAssignment.getTask()).isEqualTo(taskBack);

        taskAssignment.task(null);
        assertThat(taskAssignment.getTask()).isNull();
    }
}
