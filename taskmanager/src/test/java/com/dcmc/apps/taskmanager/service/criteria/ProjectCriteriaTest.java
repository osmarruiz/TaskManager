package com.dcmc.apps.taskmanager.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProjectCriteriaTest {

    @Test
    void newProjectCriteriaHasAllFiltersNullTest() {
        var projectCriteria = new ProjectCriteria();
        assertThat(projectCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void projectCriteriaFluentMethodsCreatesFiltersTest() {
        var projectCriteria = new ProjectCriteria();

        setAllFilters(projectCriteria);

        assertThat(projectCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void projectCriteriaCopyCreatesNullFilterTest() {
        var projectCriteria = new ProjectCriteria();
        var copy = projectCriteria.copy();

        assertThat(projectCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(projectCriteria)
        );
    }

    @Test
    void projectCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var projectCriteria = new ProjectCriteria();
        setAllFilters(projectCriteria);

        var copy = projectCriteria.copy();

        assertThat(projectCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(projectCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var projectCriteria = new ProjectCriteria();

        assertThat(projectCriteria).hasToString("ProjectCriteria{}");
    }

    private static void setAllFilters(ProjectCriteria projectCriteria) {
        projectCriteria.id();
        projectCriteria.title();
        projectCriteria.description();
        projectCriteria.startDate();
        projectCriteria.endDate();
        projectCriteria.workGroupId();
        projectCriteria.distinct();
    }

    private static Condition<ProjectCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getTitle()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getStartDate()) &&
                condition.apply(criteria.getEndDate()) &&
                condition.apply(criteria.getWorkGroupId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProjectCriteria> copyFiltersAre(ProjectCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getTitle(), copy.getTitle()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getStartDate(), copy.getStartDate()) &&
                condition.apply(criteria.getEndDate(), copy.getEndDate()) &&
                condition.apply(criteria.getWorkGroupId(), copy.getWorkGroupId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
