package io.kofun;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OptionalsTest {

    @Test
    public void equals_shouldReturnTrueIfTwoOptionalsAreEmpty() {
        // given:
        Optional<?> first = Optional.empty();
        Optional<?> second = Optional.empty();

        // when:
        boolean result = Optionals.equals(first, second);

        // then:
        assertTrue(result);
    }

    @Test
    public void equals_shouldReturnTrueIfFirstOptionalIsEmptyAndSecondIsFull() {
        // given:
        Optional<?> first = Optional.empty();
        Optional<?> second = Optional.optional(new Object());

        // when:
        boolean result = Optionals.equals(first, second);

        // then:
        assertFalse(result);
    }

    @Test
    public void equals_shouldReturnTrueIfFirstOptionalIsFullAndSecondIsEmpty() {
        // given:
        Optional<?> first = Optional.optional(new Object());
        Optional<?> second = Optional.empty();

        // when:
        boolean result = Optionals.equals(first, second);

        // then:
        assertFalse(result);
    }

    @Test
    public void equals_shouldReturnTrueIfBothOptionalsAreFullAndTheirContentsAreEqual() {
        // given:
        Optional<?> first = Optional.optional("test");
        Optional<?> second = Optional.optional("test");

        // when:
        boolean result = Optionals.equals(first, second);

        // then:
        assertTrue(result);
    }

}