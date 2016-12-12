package org.raml.utilities.iterables;

import com.google.common.base.Supplier;

public class Iterables {

    private Iterables() {}

    public static <T> Iterable<T> suplying(Supplier<? extends T> supplier) {
        return SupplierIterable.create(supplier);
    }
}
