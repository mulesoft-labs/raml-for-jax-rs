package org.raml.jaxrs.model;

import com.google.common.base.Optional;

public interface JaxRsQueryParameter {
    String getName();
    Optional<String> getDefaultValue();
}
