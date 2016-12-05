package org.raml.jaxrs.converter.model;

import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.api.RamlMediaType;
import org.raml.api.RamlApi;
import org.raml.api.RamlResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class JaxRsRamlApi implements RamlApi {
    private final RamlConfiguration configuration;
    private final JaxRsApplication application;

    private JaxRsRamlApi(RamlConfiguration configuration, JaxRsApplication application) {
        this.configuration = configuration;
        this.application = application;
    }

    public static JaxRsRamlApi create(RamlConfiguration configuration, JaxRsApplication application) {
        checkNotNull(configuration);
        checkNotNull(application);

        return new JaxRsRamlApi(configuration, application);
    }

    @Override
    public String getTitle() {
        return configuration.getTitle();
    }

    @Override
    public String getVersion() {
        return configuration.getVersion();
    }

    @Override
    public String getBaseUri() {
        return configuration.getBaseUri();
    }

    @Override
    public List<RamlResource> getResources() {
        return Utilities.toRamlResources(this.application.getResources()).toList();
    }

    @Override
    public RamlMediaType getDefaultMediaType() {
        return configuration.getDefaultMediaType();
    }
}
