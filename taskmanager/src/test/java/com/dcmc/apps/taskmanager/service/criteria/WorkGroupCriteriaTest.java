package com.dcmc.apps.taskmanager.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WorkGroupCriteriaTest {

    @Test
    void newWorkGroupCriteriaHasAllFiltersNullTest() {
        var workGroupCriteria = new WorkGroupCriteria();
        assertThat(workGroupCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void workGroupCriteriaFluentMethodsCreatesFiltersTest() {
        var workGroupCriteria = new WorkGroupCriteria();

        setAllFilters(workGroupCriteria);

        assertThat(workGroupCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void workGroupCriteriaCopyCreatesNullFilterTest() {
        var workGroupCriteria = new WorkGroupCriteria();
        var copy = workGroupCriteria.copy();

        assertThat(workGroupCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(workGroupCriteria)
        );
    }

    @Test
    void workGroupCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var workGroupCriteria = new WorkGroupCriteria();
        setAllFilters(workGroupCriteria);

        var copy = workGroupCriteria.copy();

        assertThat(workGroupCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(workGroupCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var workGroupCriteria = new WorkGroupCriteria();

        assertThat(workGroupCriteria).hasToString("WorkGroupCriteria{}");
    }

    private static void setAllFilters(WorkGroupCriteria workGroupCriteria) {
        workGroupCriteria.id();
        workGroupCriteria.name();
        workGroupCriteria.description();
        workGroupCriteria.creationDate();
        workGroupCriteria.distinct();
    }

    private static Condition<WorkGroupCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getCreationDate()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WorkGroupCriteria> copyFiltersAre(WorkGroupCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getCreationDate(), copy.getCreationDate()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
