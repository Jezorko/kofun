package ko;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

final class FullOptional<Value> implements Optional<Value> {

    private final Value value;

    FullOptional(Value value) {
        Objects.requireNonNull(value, "Null value given to non-empty optional");
        this.value = value;
    }

    @NotNull
    @Override
    public Value get() {
        return value;
    }

    @Override
    public boolean isPresent() {
        return true;
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
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "Optional[" + value + "]";
    }

}
