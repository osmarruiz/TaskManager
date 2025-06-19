package com.dcmc.apps.taskmanager.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaskStatusCatalogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TaskStatusCatalog getTaskStatusCatalogSample1() {
        return new TaskStatusCatalog().id(1L).name("name1").description("description1");
    }

    public static TaskStatusCatalog getTaskStatusCatalogSample2() {
        return new TaskStatusCatalog().id(2L).name("name2").description("description2");
    }

    public static TaskStatusCatalog getTaskStatusCatalogRandomSampleGenerator() {
        return new TaskStatusCatalog()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
