package org.raml.jaxrs.model;

import com.google.common.base.Optional;

import java.lang.reflect.Type;

public interface JaxRsHeaderParameter {
    String getName();
    Optional<String> getDefaultValue();
    Type getType();
}
