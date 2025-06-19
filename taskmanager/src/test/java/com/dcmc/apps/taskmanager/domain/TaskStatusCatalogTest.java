package com.dcmc.apps.taskmanager.domain;

import static com.dcmc.apps.taskmanager.domain.TaskStatusCatalogTestSamples.*;
import static com.dcmc.apps.taskmanager.domain.WorkGroupTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.dcmc.apps.taskmanager.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskStatusCatalogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskStatusCatalog.class);
        TaskStatusCatalog taskStatusCatalog1 = getTaskStatusCatalogSample1();
        TaskStatusCatalog taskStatusCatalog2 = new TaskStatusCatalog();
        assertThat(taskStatusCatalog1).isNotEqualTo(taskStatusCatalog2);

        taskStatusCatalog2.setId(taskStatusCatalog1.getId());
        assertThat(taskStatusCatalog1).isEqualTo(taskStatusCatalog2);

        taskStatusCatalog2 = getTaskStatusCatalogSample2();
        assertThat(taskStatusCatalog1).isNotEqualTo(taskStatusCatalog2);
    }


}
