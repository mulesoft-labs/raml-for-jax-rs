package org.raml.jaxrs.raml.core;

import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.converter.model.JaxRsRamlMediaType;
import org.raml.model.MediaType;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class DefaultRamlConfiguration implements RamlConfiguration {

    private final String application;

    private DefaultRamlConfiguration(String application) {
        this.application = application;
    }

    public static DefaultRamlConfiguration forApplication(String application) {
        checkNotNull(application);
        checkArgument(!application.trim().isEmpty(), "application name should contain at least one meaningful character");

        return new DefaultRamlConfiguration(application.trim());
    }

    @Override
    public String getTitle() {
        return "Raml API generated from " + application;
    }

    @Override
    public String getBaseUri() {
        return "http://www.baseuri.com";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public MediaType getDefaultMediaType() {
        return JaxRsRamlMediaType.create(javax.ws.rs.core.MediaType.WILDCARD_TYPE);
    }
}
