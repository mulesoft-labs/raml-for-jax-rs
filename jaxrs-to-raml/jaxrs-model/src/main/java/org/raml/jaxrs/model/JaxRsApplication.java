package org.raml.jaxrs.model;

import java.util.Set;

public interface JaxRsApplication {
    Set<Resource> getResources();
}
