package ko;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * A collection of helpful static methods relevant to {@link Optional}.
 */
public class Optionals extends StaticMethodsCollection {

    /**
     * @throws InstantiationOfStaticMethodsCollectionException on each call
     */
    @Contract(value = "-> fail", pure = true)
    private Optionals() throws InstantiationOfStaticMethodsCollectionException {
        super();
    }

    /**
     * Equality check for two {@link Optional} objects.
     *
     * @param first  object to check
     * @param second object to check against
     *
     * @return true if both optionals are present and their values are equal or if both are absent, false otherwise
     */
    public static boolean equals(Optional first, Optional second) {
        if (!first.isPresent() && !second.isPresent()) {
            return true;
        }
        else if (first.isPresent() && second.isPresent()) {
            return Objects.equals(first.get(), second.get());
        }
        else {
            return false;
        }
    }

}
