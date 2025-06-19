package com.dcmc.apps.taskmanager.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TaskAssignmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TaskAssignment getTaskAssignmentSample1() {
        return new TaskAssignment().id(1L);
    }

    public static TaskAssignment getTaskAssignmentSample2() {
        return new TaskAssignment().id(2L);
    }

    public static TaskAssignment getTaskAssignmentRandomSampleGenerator() {
        return new TaskAssignment().id(longCount.incrementAndGet());
    }
}
