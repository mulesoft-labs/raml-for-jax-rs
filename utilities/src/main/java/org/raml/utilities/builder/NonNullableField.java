package org.raml.utilities.builder;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class NonNullableField<T> implements Field<T> {
    private final T payload;

    private NonNullableField(T payload) {
        this.payload = payload;
    }

    public static <T> NonNullableField<T> unset() {
        return new NonNullableField<>(null);
    }

    public static <T> NonNullableField<T> of(T value) {
        checkNotNull(value);

        return new NonNullableField<>(value);
    }

    public static <T> NonNullableField<T> ofNullable(T value) {
        return value == null? NonNullableField.<T>unset() : of(value);
    }

    @Override
    public T get() {
        checkState(isSet(), "value not set");

        return payload;
    }

    @Override
    public boolean isSet() {
        return payload != null;
    }
}
