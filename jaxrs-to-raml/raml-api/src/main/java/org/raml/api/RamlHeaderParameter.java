package org.raml.api;

import com.google.common.base.Optional;

import java.lang.reflect.Type;

public interface RamlHeaderParameter {
    String getName();
    Optional<String> getDefaultValue();
    Type getType();
}
