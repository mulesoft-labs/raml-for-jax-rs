package org.raml.api;

import com.google.common.base.Optional;

import java.util.List;

public interface RamlResourceMethod {
    String getHttpMethod();
    List<RamlMediaType> getConsumedMediaTypes();
    List<RamlMediaType> getProducedMediaTypes();
    List<RamlQueryParameter> getQueryParameters();
    List<RamlHeaderParameter> getHeaderParameters();
    Optional<String> getDescription();
}
