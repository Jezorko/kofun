package io.kofun.prototypes;

import org.jetbrains.annotations.NotNull;

/**
 * Marker interface for fluent prototypes.
 *
 * @param <NewFluentPrototypeType> the generic "this" type returned from method chains
 */
public interface FluentPrototype<NewFluentPrototypeType extends FluentPrototype> {

    /**
     * Allows a type change of the current {@link FluentPrototype} object.
     * This method is used by methods marked with {@link ExtensibleFluentChain}
     * when creating a new instance is not necessary, but the current
     * object's type must be changed in order to comply with the generic type.
     * This method does not need to be reimplemented for a subtype to comply
     * with the {@link ExtensibleFluentChain} contract.
     *
     * @return the same instance of the {@link FluentPrototype} object with type specified by the generic argument
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default NewFluentPrototypeType retype() {
        return (NewFluentPrototypeType) this;
    }

}
