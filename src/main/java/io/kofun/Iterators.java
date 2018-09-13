package io.kofun;

import org.jetbrains.annotations.Contract;

import java.util.Collections;
import java.util.Iterator;

public class Iterators extends StaticMethodsCollection {

    /**
     * @throws InstantiationOfStaticMethodsCollectionException on each call
     */
    @Contract(value = "-> fail", pure = true)
    private Iterators() throws InstantiationOfStaticMethodsCollectionException {
        super();
    }

    public static <T> Iterator<T> singleton(T value) {
        return Collections.singleton(value)
                          .iterator();
    }

    public static <T> Iterator<T> emptyIterator() {
        return Collections.emptyIterator();
    }

}
