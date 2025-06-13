package com.dcmc.apps.taskmanager.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class WorkGroupMembershipCriteriaTest {

    @Test
    void newWorkGroupMembershipCriteriaHasAllFiltersNullTest() {
        var workGroupMembershipCriteria = new WorkGroupMembershipCriteria();
        assertThat(workGroupMembershipCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void workGroupMembershipCriteriaFluentMethodsCreatesFiltersTest() {
        var workGroupMembershipCriteria = new WorkGroupMembershipCriteria();

        setAllFilters(workGroupMembershipCriteria);

        assertThat(workGroupMembershipCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void workGroupMembershipCriteriaCopyCreatesNullFilterTest() {
        var workGroupMembershipCriteria = new WorkGroupMembershipCriteria();
        var copy = workGroupMembershipCriteria.copy();

        assertThat(workGroupMembershipCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(workGroupMembershipCriteria)
        );
    }

    @Test
    void workGroupMembershipCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var workGroupMembershipCriteria = new WorkGroupMembershipCriteria();
        setAllFilters(workGroupMembershipCriteria);

        var copy = workGroupMembershipCriteria.copy();

        assertThat(workGroupMembershipCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(workGroupMembershipCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var workGroupMembershipCriteria = new WorkGroupMembershipCriteria();

        assertThat(workGroupMembershipCriteria).hasToString("WorkGroupMembershipCriteria{}");
    }

    private static void setAllFilters(WorkGroupMembershipCriteria workGroupMembershipCriteria) {
        workGroupMembershipCriteria.id();
        workGroupMembershipCriteria.role();
        workGroupMembershipCriteria.joinDate();
        workGroupMembershipCriteria.userId();
        workGroupMembershipCriteria.workGroupId();
        workGroupMembershipCriteria.distinct();
    }

    private static Condition<WorkGroupMembershipCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getRole()) &&
                condition.apply(criteria.getJoinDate()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getWorkGroupId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<WorkGroupMembershipCriteria> copyFiltersAre(
        WorkGroupMembershipCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getRole(), copy.getRole()) &&
                condition.apply(criteria.getJoinDate(), copy.getJoinDate()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getWorkGroupId(), copy.getWorkGroupId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
