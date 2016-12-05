package org.raml.api;

import java.util.List;

public interface RamlResourceMethod {
    String getHttpMethod();
    List<RamlMediaType> getConsumedMediaTypes();
    List<RamlMediaType> getProducedMediaTypes();
    List<RamlQueryParameter> getQueryParameters();
}
