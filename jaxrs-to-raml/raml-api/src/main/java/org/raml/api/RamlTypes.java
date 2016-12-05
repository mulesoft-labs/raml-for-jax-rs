package org.raml.api;

import com.google.common.base.Optional;

import java.lang.reflect.Type;

import static java.lang.String.format;

public class RamlTypes {

    public static RamlType fromType(Type type) {
        Optional<ScalarType> scalarTypeOptional = ScalarType.fromType(type);

        if (scalarTypeOptional.isPresent()) {
            return scalarTypeOptional.get();
        }

        throw new RuntimeException(format("unknown type: %s", type));
    }



}
