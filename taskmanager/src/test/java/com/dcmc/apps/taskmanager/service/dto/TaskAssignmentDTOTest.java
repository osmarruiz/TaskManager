package com.dcmc.apps.taskmanager.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dcmc.apps.taskmanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskAssignmentDTO.class);
        TaskAssignmentDTO taskAssignmentDTO1 = new TaskAssignmentDTO();
        taskAssignmentDTO1.setId(1L);
        TaskAssignmentDTO taskAssignmentDTO2 = new TaskAssignmentDTO();
        assertThat(taskAssignmentDTO1).isNotEqualTo(taskAssignmentDTO2);
        taskAssignmentDTO2.setId(taskAssignmentDTO1.getId());
        assertThat(taskAssignmentDTO1).isEqualTo(taskAssignmentDTO2);
        taskAssignmentDTO2.setId(2L);
        assertThat(taskAssignmentDTO1).isNotEqualTo(taskAssignmentDTO2);
        taskAssignmentDTO1.setId(null);
        assertThat(taskAssignmentDTO1).isNotEqualTo(taskAssignmentDTO2);
    }
}
