package com.dcmc.apps.taskmanager.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class TaskStatusCatalogCriteriaTest {

    @Test
    void newTaskStatusCatalogCriteriaHasAllFiltersNullTest() {
        var taskStatusCatalogCriteria = new TaskStatusCatalogCriteria();
        assertThat(taskStatusCatalogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void taskStatusCatalogCriteriaFluentMethodsCreatesFiltersTest() {
        var taskStatusCatalogCriteria = new TaskStatusCatalogCriteria();

        setAllFilters(taskStatusCatalogCriteria);

        assertThat(taskStatusCatalogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void taskStatusCatalogCriteriaCopyCreatesNullFilterTest() {
        var taskStatusCatalogCriteria = new TaskStatusCatalogCriteria();
        var copy = taskStatusCatalogCriteria.copy();

        assertThat(taskStatusCatalogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(taskStatusCatalogCriteria)
        );
    }

    @Test
    void taskStatusCatalogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var taskStatusCatalogCriteria = new TaskStatusCatalogCriteria();
        setAllFilters(taskStatusCatalogCriteria);

        var copy = taskStatusCatalogCriteria.copy();

        assertThat(taskStatusCatalogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(taskStatusCatalogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var taskStatusCatalogCriteria = new TaskStatusCatalogCriteria();

        assertThat(taskStatusCatalogCriteria).hasToString("TaskStatusCatalogCriteria{}");
    }

    private static void setAllFilters(TaskStatusCatalogCriteria taskStatusCatalogCriteria) {
        taskStatusCatalogCriteria.id();
        taskStatusCatalogCriteria.name();
        taskStatusCatalogCriteria.description();
        taskStatusCatalogCriteria.createdAt();
        taskStatusCatalogCriteria.updatedAt();
        taskStatusCatalogCriteria.workGroupId();
        taskStatusCatalogCriteria.createdById();
        taskStatusCatalogCriteria.distinct();
    }

    private static Condition<TaskStatusCatalogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getWorkGroupId()) &&
                condition.apply(criteria.getCreatedById()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<TaskStatusCatalogCriteria> copyFiltersAre(
        TaskStatusCatalogCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getWorkGroupId(), copy.getWorkGroupId()) &&
                condition.apply(criteria.getCreatedById(), copy.getCreatedById()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
