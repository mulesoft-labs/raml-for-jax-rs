package org.raml.jaxrs.model;

import com.google.common.base.Optional;

import java.util.List;

import javax.ws.rs.core.MediaType;

public interface JaxRsMethod {
    HttpVerb getHttpVerb();
    List<MediaType> getConsumedMediaTypes();
    List<MediaType> getProducedMediaTypes();
    List<JaxRsQueryParameter> getQueryParameters();
    List<JaxRsHeaderParameter> getHeaderParameters();
    Optional<String> getDescription();
}
