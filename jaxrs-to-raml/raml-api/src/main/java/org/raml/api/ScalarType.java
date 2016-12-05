package org.raml.api;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.util.Map;

public enum ScalarType implements RamlType {
    NUMBER("number", ImmutableList.<Type>of(float.class, Float.class, double.class, Double.class)),
    BOOLEAN("boolean", ImmutableList.<Type>of(boolean.class, Boolean.class)),
    STRING("string", ImmutableList.<Type>of(String.class)),
    DATE_ONLY("date-only", ImmutableList.<Type>of()),
    TIME_ONLY("time-only", ImmutableList.<Type>of()),
    DATETIME_ONLY("datetime-only", ImmutableList.<Type>of()),
    DATETIME("datetime", ImmutableList.<Type>of()),
    FILE("file", ImmutableList.<Type>of()),
    //All "integer" types are mapped to raml's "integer". Not sure if that is correct.
    INTEGER("integer", ImmutableList.<Type>of(int.class, Integer.class, byte.class, Byte.class, short.class,
            Short.class, long.class, Long.class)),
    NIL("nil", ImmutableList.<Type>of());

    private static final Map<Type, ScalarType> JAVA_TO_RAML_TYPES;

    static {
        ImmutableMap.Builder<Type, ScalarType> builder = ImmutableMap.builder();

        for (ScalarType ramlType : ScalarType.values()) {
            for (Type type : ramlType.correspondingJavaTypes) {
                builder.put(type, ramlType);
            }
        }

        JAVA_TO_RAML_TYPES = builder.build();
    }

    private final String ramlSyntax;
    private final Iterable<Type> correspondingJavaTypes;

    ScalarType(String ramlSyntax, Iterable<Type> correspondingJavaTypes) {
        this.ramlSyntax = ramlSyntax;
        this.correspondingJavaTypes = correspondingJavaTypes;
    }

    @Override
    public String getRamlSyntax() {
        return ramlSyntax;
    }

    public static Optional<ScalarType> fromType(Type type) {
        return Optional.fromNullable(JAVA_TO_RAML_TYPES.get(type));
    }
}
