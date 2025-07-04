package com.dcmc.apps.taskmanager.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class ProjectMemberCriteriaTest {

    @Test
    void newProjectMemberCriteriaHasAllFiltersNullTest() {
        var projectMemberCriteria = new ProjectMemberCriteria();
        assertThat(projectMemberCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void projectMemberCriteriaFluentMethodsCreatesFiltersTest() {
        var projectMemberCriteria = new ProjectMemberCriteria();

        setAllFilters(projectMemberCriteria);

        assertThat(projectMemberCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void projectMemberCriteriaCopyCreatesNullFilterTest() {
        var projectMemberCriteria = new ProjectMemberCriteria();
        var copy = projectMemberCriteria.copy();

        assertThat(projectMemberCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(projectMemberCriteria)
        );
    }

    @Test
    void projectMemberCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var projectMemberCriteria = new ProjectMemberCriteria();
        setAllFilters(projectMemberCriteria);

        var copy = projectMemberCriteria.copy();

        assertThat(projectMemberCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(projectMemberCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var projectMemberCriteria = new ProjectMemberCriteria();

        assertThat(projectMemberCriteria).hasToString("ProjectMemberCriteria{}");
    }

    private static void setAllFilters(ProjectMemberCriteria projectMemberCriteria) {
        projectMemberCriteria.id();
        projectMemberCriteria.assignedAt();
        projectMemberCriteria.projectId();
        projectMemberCriteria.userId();
        projectMemberCriteria.distinct();
    }

    private static Condition<ProjectMemberCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getAssignedAt()) &&
                condition.apply(criteria.getProjectId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<ProjectMemberCriteria> copyFiltersAre(
        ProjectMemberCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getAssignedAt(), copy.getAssignedAt()) &&
                condition.apply(criteria.getProjectId(), copy.getProjectId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
