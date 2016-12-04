package org.raml.model;

import java.util.List;

public interface ResourceMethod {
    String getHttpMethod();
    List<MediaType> getConsumedMediaTypes();
    List<MediaType> getProducedMediaTypes();
    List<RamlQueryParameter> getQueryParameters();
}
