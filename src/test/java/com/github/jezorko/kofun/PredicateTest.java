package com.github.jezorko.kofun;

import org.junit.Test;

import java.util.Objects;

import static com.github.jezorko.kofun.Predicate.from;
import static com.github.jezorko.kofun.AssertionUtils.assertMatchesTruthTable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {

    @Test
    public void from_shouldMapPredicateToAdvancedPredicateEquivalent() {
        assertTrue(from(Objects::nonNull).test(new Object()));
        assertTrue(from(Objects::isNull).test(null));
        assertFalse(from(Objects::isNull).test(new Object()));
        assertFalse(from(Objects::nonNull).test(null));
    }

    @Test
    public void xor_shouldMatchTheTruthTable() {
        // expect:
        assertMatchesTruthTable(table -> table.withRow(0, 0, 0)
                                              .withRow(0, 1, 1)
                                              .withRow(1, 0, 1)
                                              .withRow(1, 1, 0),
                                Predicate::xor);
    }

    @Test
    public void nand_shouldMatchTheTruthTable() {
        // expect:
        assertMatchesTruthTable(table -> table.withRow(0, 0, 1)
                                              .withRow(0, 1, 1)
                                              .withRow(1, 0, 1)
                                              .withRow(1, 1, 0),
                                Predicate::nand);
    }

    @Test
    public void nor_shouldMatchTheTruthTable() {
        // expect:
        assertMatchesTruthTable(table -> table.withRow(0, 0, 1)
                                              .withRow(0, 1, 0)
                                              .withRow(1, 0, 0)
                                              .withRow(1, 1, 0),
                                Predicate::nor);
    }

}