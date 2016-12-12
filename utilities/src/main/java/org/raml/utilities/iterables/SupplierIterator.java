package org.raml.utilities.iterables;

import com.google.common.base.Supplier;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkNotNull;

class SupplierIterator<T> implements Iterator<T> {
    private final Supplier<? extends T> supplier;

    private SupplierIterator(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    public static <T> SupplierIterator<T> create(Supplier<? extends T> supplier) {
        checkNotNull(supplier);

        return new SupplierIterator(supplier);
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T next() {
        return supplier.get();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("cannot remove on a " + this.getClass().getSimpleName());
    }
}
