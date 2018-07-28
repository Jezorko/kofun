package io.kofun;

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FullOptionalTest {

    @Test(expected = NullPointerException.class)
    public void constructor_shouldThrowIfConstructedWithNull() {
        new FullOptional<>(null);
    }

    @Test
    public void isPresent_shouldAlwaysReturnTrue() {
        // given:
        Object anyObject = new Object();
        Optional<Object> fullOptional = new FullOptional<>(anyObject);

        // expect:
        assertTrue(fullOptional.isPresent());
    }

    @Test
    public void get_shouldAlwaysReturnValue() {
        // given:
        Object anyObject = new Object();
        Optional<Object> fullOptional = new FullOptional<>(anyObject);

        // expect:
        assertSame(anyObject, fullOptional.get());
    }

}