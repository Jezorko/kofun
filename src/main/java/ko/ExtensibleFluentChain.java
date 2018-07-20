package ko;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Methods marked with this annotation are transforming the
 * object's implementation in a fluent manner to allow
 * its' extension with additional methods.
 * Consider the following example:<br/>
 * An interface called "Wrapper" contains a method "chain"
 * which returns a generic type &#x3C;WrapperType extends Wrapper&#x3E;.
 * An extension to this interface called "WrapperExtension" that
 * contains an additional method "chainExtended" can now be accessed
 * after using the "chain" methods in a context where wrapper's type
 * is known and if the "WrapperExtension" returns a
 * &#x3C;WrapperType extends WrapperExtension&#x3E;.<br/>
 * The code sample below illustrates this behavior:
 * <pre>
 * new WrapperExtension().chain()
 *                       .chainExtended();
 * </pre>
 * To enable this behavior, all methods annotated with
 * {@link ExtensibleFluentChain} must be implemented
 * and their generic return type changed.
 * Otherwise the Java compiler will not have enough information
 * about the type and will not allow for calling the extended methods.
 */
@Documented
@Target(METHOD)
@Retention(CLASS)
public @interface ExtensibleFluentChain {
}
