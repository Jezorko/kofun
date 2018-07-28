package io.kofun.prototypes;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Methods marked with this annotation are transforming the
 * object's implementation in a fluent manner to allow
 * its' extension with additional methods.
 * Consider the following example:<br/>
 * An interface called "Fluent" has a generic parameter "NewFluentType" and
 * a "chain" method that returns it.
 * An extension to this interface called "FluentExtension" that
 * contains an additional method "chainExtended" can now be accessed
 * after using the "chain" methods in a context where the "NewFluentType"
 * is known and if the "FluentExtension" returns a "FluentExtension".<br/>
 * The code sample below illustrates this behavior:
 * <pre>
 * new FluentExtension().chain()
 *                      .chainExtended()
 *                      .chain()
 *                      .chainExtended();
 * </pre>
 * To enable this behavior, all methods annotated with
 * {@link ExtensibleFluentChain} must be implemented
 * and their generic return type changed.
 * Otherwise the Java compiler will not have enough information
 * about the type and will not allow for calling the extended methods.
 * For more details refer to <a href="https://github.com/Jezorko/kofun/issues/8#issuecomment-406801543">this issue</a>.
 * All classes in {@link ko.prototypes} employ those semantics.
 */
@Documented
@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtensibleFluentChain {
}
