package com.dcmc.apps.taskmanager.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PriorityCriteriaTest {

    @Test
    void newPriorityCriteriaHasAllFiltersNullTest() {
        var priorityCriteria = new PriorityCriteria();
        assertThat(priorityCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void priorityCriteriaFluentMethodsCreatesFiltersTest() {
        var priorityCriteria = new PriorityCriteria();

        setAllFilters(priorityCriteria);

        assertThat(priorityCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void priorityCriteriaCopyCreatesNullFilterTest() {
        var priorityCriteria = new PriorityCriteria();
        var copy = priorityCriteria.copy();

        assertThat(priorityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(priorityCriteria)
        );
    }

    @Test
    void priorityCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var priorityCriteria = new PriorityCriteria();
        setAllFilters(priorityCriteria);

        var copy = priorityCriteria.copy();

        assertThat(priorityCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(priorityCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var priorityCriteria = new PriorityCriteria();

        assertThat(priorityCriteria).hasToString("PriorityCriteria{}");
    }

    private static void setAllFilters(PriorityCriteria priorityCriteria) {
        priorityCriteria.id();
        priorityCriteria.name();
        priorityCriteria.description();
        priorityCriteria.visible();
        priorityCriteria.createdAt();
        priorityCriteria.updatedAt();
        priorityCriteria.distinct();
    }

    private static Condition<PriorityCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getDescription()) &&
                condition.apply(criteria.getVisible()) &&
                condition.apply(criteria.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PriorityCriteria> copyFiltersAre(PriorityCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getDescription(), copy.getDescription()) &&
                condition.apply(criteria.getVisible(), copy.getVisible()) &&
                condition.apply(criteria.getCreatedAt(), copy.getCreatedAt()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
