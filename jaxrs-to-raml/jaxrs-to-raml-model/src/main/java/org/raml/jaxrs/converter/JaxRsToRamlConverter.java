package org.raml.jaxrs.converter;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.model.RamlApi;
import org.raml.model.impl.RamlApiImpl;

public class JaxRsToRamlConverter {

    private JaxRsToRamlConverter() {}

    public static JaxRsToRamlConverter create() {
        return new JaxRsToRamlConverter();
    }

    public RamlApi convert(RamlConfiguration configuration, JaxRsApplication application) throws JaxRsToRamlConversionException {

        return RamlApiImpl.create(configuration.getTitle(), configuration.getVersion(), configuration.getBaseUri());
    }

}
