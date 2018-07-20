package ko;

import org.jetbrains.annotations.Contract;

import javax.naming.OperationNotSupportedException;

/**
 * A non-instantiable collection of static methods.
 * Objects of classes derived from this class can never be created.
 */
class StaticMethodsCollection {

    /**
     * @throws InstantiationOfStaticMethodsCollectionException on each call
     */
    @Contract(value = "-> fail", pure = true)
    StaticMethodsCollection() throws InstantiationOfStaticMethodsCollectionException {
        throw new InstantiationOfStaticMethodsCollectionException(getClass());
    }

    /**
     * Thrown when an attempt is made to create a {@link StaticMethodsCollection}.
     */
    static final class InstantiationOfStaticMethodsCollectionException extends OperationNotSupportedException {

        private InstantiationOfStaticMethodsCollectionException(Class<?> staticMethodsCollectionClass) {
            super("Class " + staticMethodsCollectionClass.toString() + " is not meant to be instantiated");
        }

    }

}
