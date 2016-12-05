package org.raml.jaxrs.model;

import java.util.List;

import javax.ws.rs.core.MediaType;

public interface Method {
    HttpVerb getHttpVerb();
    List<MediaType> getConsumedMediaTypes();
    List<MediaType> getProducedMediaTypes();
    List<JaxRsQueryParameter> getQueryParameters();
    List<JaxRsHeaderParameter> getHeaderParameters();
}
