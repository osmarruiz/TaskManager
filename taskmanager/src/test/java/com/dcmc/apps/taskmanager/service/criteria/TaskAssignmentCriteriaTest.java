package com.dcmc.apps.taskmanager.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TaskAssignmentCriteriaTest {

    @Test
    void newTaskAssignmentCriteriaHasAllFiltersNullTest() {
        var taskAssignmentCriteria = new TaskAssignmentCriteria();
        assertThat(taskAssignmentCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void taskAssignmentCriteriaFluentMethodsCreatesFiltersTest() {
        var taskAssignmentCriteria = new TaskAssignmentCriteria();

        setAllFilters(taskAssignmentCriteria);

        assertThat(taskAssignmentCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void taskAssignmentCriteriaCopyCreatesNullFilterTest() {
        var taskAssignmentCriteria = new TaskAssignmentCriteria();
        var copy = taskAssignmentCriteria.copy();

        assertThat(taskAssignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(taskAssignmentCriteria)
        );
    }

    @Test
    void taskAssignmentCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var taskAssignmentCriteria = new TaskAssignmentCriteria();
        setAllFilters(taskAssignmentCriteria);

        var copy = taskAssignmentCriteria.copy();

        assertThat(taskAssignmentCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(taskAssignmentCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var taskAssignmentCriteria = new TaskAssignmentCriteria();

        assertThat(taskAssignmentCriteria).hasToString("TaskAssignmentCriteria{}");
    }

    private static void setAllFilters(TaskAssignmentCriteria taskAssignmentCriteria) {
        taskAssignmentCriteria.id();
        taskAssignmentCriteria.assignedAt();
        taskAssignmentCriteria.taskId();
        taskAssignmentCriteria.userId();
        taskAssignmentCriteria.distinct();
    }

    private static Condition<TaskAssignmentCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAssignedAt()) &&
                condition.apply(criteria.getTaskId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TaskAssignmentCriteria> copyFiltersAre(
        TaskAssignmentCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAssignedAt(), copy.getAssignedAt()) &&
                condition.apply(criteria.getTaskId(), copy.getTaskId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
