package com.dcmc.apps.taskmanager.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.dcmc.apps.taskmanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskStatusCatalogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskStatusCatalogDTO.class);
        TaskStatusCatalogDTO taskStatusCatalogDTO1 = new TaskStatusCatalogDTO();
        taskStatusCatalogDTO1.setId(1L);
        TaskStatusCatalogDTO taskStatusCatalogDTO2 = new TaskStatusCatalogDTO();
        assertThat(taskStatusCatalogDTO1).isNotEqualTo(taskStatusCatalogDTO2);
        taskStatusCatalogDTO2.setId(taskStatusCatalogDTO1.getId());
        assertThat(taskStatusCatalogDTO1).isEqualTo(taskStatusCatalogDTO2);
        taskStatusCatalogDTO2.setId(2L);
        assertThat(taskStatusCatalogDTO1).isNotEqualTo(taskStatusCatalogDTO2);
        taskStatusCatalogDTO1.setId(null);
        assertThat(taskStatusCatalogDTO1).isNotEqualTo(taskStatusCatalogDTO2);
    }
}
