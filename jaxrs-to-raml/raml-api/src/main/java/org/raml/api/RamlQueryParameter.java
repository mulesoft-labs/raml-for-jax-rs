package org.raml.api;

import com.google.common.base.Optional;

import java.lang.reflect.Type;

public interface RamlQueryParameter {
    String getName();
    Optional<String> getDefaultValue();
    Type getType();
}
