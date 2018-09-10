package io.kofun;

import io.kofun.prototypes.ExtensibleFluentChain;
import io.kofun.prototypes.FluentPrototype;
import org.jetbrains.annotations.Contract;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Aids fluent prototype implementation by checking if all methods marked with
 * {@link ExtensibleFluentChain} annotation were reimplemented.
 */
public class ExtensibleFluentChainTestUtil extends StaticMethodsCollection {

    /**
     * @throws InstantiationOfStaticMethodsCollectionException on each call
     */
    @Contract(value = "-> fail", pure = true)
    private ExtensibleFluentChainTestUtil() throws InstantiationOfStaticMethodsCollectionException {
        super();
    }

    /**
     * Creates a prototype-implementation pair to be used for testing.
     *
     * @param prototypeClass               class of given {@link FluentPrototype}
     * @param prototypeImplementationClass class of implementation for given prototype
     * @param <PrototypeType>              the type of prototype
     *
     * @return a new instance of {@link PrototypeImplementationPair}
     */
    public static <PrototypeType extends FluentPrototype> PrototypeImplementationPair prototypeImplementation(Class<PrototypeType> prototypeClass,
                                                                                                              Class<? extends PrototypeType> prototypeImplementationClass) {
        return new PrototypeImplementationPair(prototypeClass, prototypeImplementationClass);
    }

    /**
     * Tests if implementations reimplement all methods from the prototype annotated with {@link ExtensibleFluentChain}.
     *
     * @param prototypesAndImplementations to test against
     */
    public static void shouldReimplementAllExtensibleFluentChainMethods(PrototypeImplementationPair... prototypesAndImplementations) {
        Arrays.stream(prototypesAndImplementations)
              .map(ExtensibleFluentChainTestUtil::getNotImplementedFluentChainMethods)
              .filter(m -> !m.notImplementedMethods.isEmpty())
              .peek(ExtensibleFluentChainTestUtil::logNotImplementedMethodInformation)
              .collect(toList())
              .forEach(m -> {throw new ExtensibleFluentChainMethodImplementationMissing();});
    }

    private static NotImplementedMethodInformation getNotImplementedFluentChainMethods(PrototypeImplementationPair prototypeAndImplementation) {
        final List<Method> notImplementedMethods = Arrays.stream(prototypeAndImplementation.prototypeClass.getMethods())
                                                         .filter(m -> m.isAnnotationPresent(ExtensibleFluentChain.class))
                                                         .filter(m -> !isImplementedBy(m, prototypeAndImplementation.prototypeImplementationClass))
                                                         .collect(toList());

        return new NotImplementedMethodInformation(prototypeAndImplementation.prototypeImplementationClass, notImplementedMethods);
    }

    private static void logNotImplementedMethodInformation(NotImplementedMethodInformation information) {
        System.err.println(information.prototypeImplementationClass + " requires reimplementation of the following methods:");
        information.notImplementedMethods.stream()
                                         .map(Method::toString)
                                         .map(method -> "\t" + method)
                                         .forEach(System.err::println);
    }

    private static boolean isImplementedBy(Method method, Class<?> subClass) {
        try {
            return subClass.getMethod(method.getName(), method.getParameterTypes())
                           .getDeclaringClass() == subClass;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * A pair representing a {@link FluentPrototype} class and its' implementation class.
     * @implNote keep public, otherwise compiler will complain about generic types
     */
    public final static class PrototypeImplementationPair {
        private final Class<? extends FluentPrototype> prototypeClass;
        private final Class<?> prototypeImplementationClass;

        private <PrototypeType extends FluentPrototype> PrototypeImplementationPair(Class<PrototypeType> prototypeClass,
                                                                                    Class<? extends PrototypeType> prototypeImplementationClass) {
            this.prototypeClass = prototypeClass;
            this.prototypeImplementationClass = prototypeImplementationClass;
        }
    }

    private final static class NotImplementedMethodInformation {
        private final Class<?> prototypeImplementationClass;
        private final List<Method> notImplementedMethods;

        private NotImplementedMethodInformation(Class<?> prototypeImplementationClass, List<Method> notImplementedMethods) {
            this.prototypeImplementationClass = prototypeImplementationClass;
            this.notImplementedMethods = notImplementedMethods;
        }
    }

    private final static class ExtensibleFluentChainMethodImplementationMissing extends RuntimeException {
        private ExtensibleFluentChainMethodImplementationMissing() {
            super("Not all methods marked with " + ExtensibleFluentChain.class + " annotation are implemented");
        }
    }

}
