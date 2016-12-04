package org.raml.jaxrs.converter.model;

import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.model.MediaType;
import org.raml.model.RamlApi;
import org.raml.model.Resource;

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
    public List<Resource> getResources() {
        return Utilities.toRamlResources(this.application.getResources()).toList();
    }

    @Override
    public MediaType getDefaultMediaType() {
        return configuration.getDefaultMediaType();
    }
}
