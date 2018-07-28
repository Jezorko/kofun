package io.kofun;

import io.kofun.prototypes.OptionalPrototype;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Objects;

final class EmptyOptional<ValueType> implements Optional<ValueType> {

    @NotNull
    @Override
    public ValueType get() {
        throw new NoSuchElementException("No value present");
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        else if (!(other instanceof OptionalPrototype)) {
            return false;
        }
        else {
            return Optionals.equals(this, (OptionalPrototype) other);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(null);
    }

    @Override
    public String toString() {
        return "Optional.empty";
    }

}
