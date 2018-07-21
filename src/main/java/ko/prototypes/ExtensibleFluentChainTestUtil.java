package ko.prototypes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Aids fluent prototype implementation by checking if all methods marked with
 * {@link ExtensibleFluentChain} annotation were reimplemented.
 */
public class ExtensibleFluentChainTestUtil {

    public static <PrototypeType extends FluentPrototype> PrototypeImplementationPair prototypeImplementation(Class<PrototypeType> prototypeClass,
                                                                                                              Class<? extends PrototypeType> prototypeImplementationClass) {
        return new PrototypeImplementationPair(prototypeClass, prototypeImplementationClass);
    }

    /**
     * Tests if given pairs conform to {@link ExtensibleFluentChain} contract.
     *
     * @param prototypesAndImplementations to test against
     */
    public static void test(PrototypeImplementationPair... prototypesAndImplementations) {
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
        public ExtensibleFluentChainMethodImplementationMissing() {
            super("Not all methods marked with " + ExtensibleFluentChain.class + " annotation are implemented");
        }
    }

}
