package org.raml.jaxrs.parser.application;

import java.util.Set;

public interface JaxRsApplication {
    Set<Resource> getResources();
}
