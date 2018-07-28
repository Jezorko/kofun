package io.kofun;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {

    @Test
    public void from_shouldMapPredicateToAdvancedPredicateEquivalent() {
        assertTrue(Predicate.from(Objects::nonNull).test(new Object()));
        assertTrue(Predicate.from(Objects::isNull).test(null));
        assertFalse(Predicate.from(Objects::isNull).test(new Object()));
        assertFalse(Predicate.from(Objects::nonNull).test(null));
    }

    @Test
    public void xor_shouldMatchTheTruthTable() {
        // expect:
        AssertionUtils.assertMatchesTruthTable(table -> table.withRow(0, 0, 0)
                                                             .withRow(0, 1, 1)
                                                             .withRow(1, 0, 1)
                                                             .withRow(1, 1, 0),
                                               Predicate::xor);
    }

    @Test
    public void nand_shouldMatchTheTruthTable() {
        // expect:
        AssertionUtils.assertMatchesTruthTable(table -> table.withRow(0, 0, 1)
                                                             .withRow(0, 1, 1)
                                                             .withRow(1, 0, 1)
                                                             .withRow(1, 1, 0),
                                               Predicate::nand);
    }

    @Test
    public void nor_shouldMatchTheTruthTable() {
        // expect:
        AssertionUtils.assertMatchesTruthTable(table -> table.withRow(0, 0, 1)
                                                             .withRow(0, 1, 0)
                                                             .withRow(1, 0, 0)
                                                             .withRow(1, 1, 0),
                                               Predicate::nor);
    }

}