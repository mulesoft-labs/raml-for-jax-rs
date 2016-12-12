package org.raml.utilities.iterables;

import com.google.common.base.Supplier;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

class SupplierIterable<T> implements Iterable<T> {

    private final Supplier<? extends T> supplier;

    private SupplierIterable(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    public static <T> SupplierIterable<T> create(Supplier<? extends T> supplier) {
        checkNotNull(supplier);

        return new SupplierIterable<>(supplier);
    }

    @Override
    public Iterator<T> iterator() {
        return SupplierIterator.create(this.supplier);
    }
}
