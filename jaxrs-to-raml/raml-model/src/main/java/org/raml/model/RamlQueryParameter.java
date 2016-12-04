package org.raml.model;

import com.google.common.base.Optional;

public interface RamlQueryParameter {
    String getName();
    Optional<String> getDefaultValue();
}
