package ko;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Objects;

final class EmptyOptional<Value> implements Optional<Value> {

    @NotNull
    @Override
    public Value get() {
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
        else if (!(other instanceof Optional)) {
            return false;
        }
        else {
            return Optionals.equals(this, (Optional) other);
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
