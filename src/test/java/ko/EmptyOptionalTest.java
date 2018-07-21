package ko;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EmptyOptionalTest {

    @Test
    public void isPresent_shouldAlwaysReturnFalse() {
        // given:
        Optional<Object> emptyOptional = new EmptyOptional<>();

        // expect:
        assertFalse(emptyOptional.isPresent());
    }

    @Test(expected = NoSuchElementException.class)
    public void get_shouldAlwaysThrowException() {
        new EmptyOptional().get();
    }

    @Test
    public void equals_shouldReturnTrueIfOtherOptionalIsEmptyEvenIfItIsNotAnInstanceOfEmptyOptional() {
        // given:
        Optional<Object> emptyOptional = new EmptyOptional<>();
        Optional<Object> otherEmptyOptional = new Optional<Object>() {
            @NotNull
            @Override
            public Object get() {
                return null;
            }

            @Override
            public boolean isPresent() {
                return false;
            }
        };

        // expect:
        assertTrue(emptyOptional.equals(otherEmptyOptional));
    }

}