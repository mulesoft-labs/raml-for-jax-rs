package org.raml.model;

import java.util.List;

public interface ResourceMethod {
    String getString();
    List<MediaType> getConsumedMediaTypes();
}
