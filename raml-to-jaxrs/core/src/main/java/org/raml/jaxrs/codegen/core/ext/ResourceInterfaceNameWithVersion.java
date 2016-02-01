package org.raml.jaxrs.codegen.core.ext;

import org.raml.jaxrs.codegen.core.Configuration;
import org.raml.jaxrs.codegen.core.Names;
import org.raml.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceInterfaceNameWithVersion extends AbstractGeneratorExtension implements InterfaceNameBuilderExtension {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceInterfaceNameWithVersion.class);

    @Override
    public String buildResourceInterfaceName(Resource resource) {
        Configuration config = new Configuration();
        config.setInterfaceNameSuffix("Resource");
        String resourceName = Names.buildResourceInterfaceName(resource, config);
        String version = getRaml().getVersion();
        if (version != null) {
            resourceName += version.toUpperCase();
            LOGGER.debug("Generated resource name: " + resourceName);
            return resourceName;
        }
        String message = "No version found in RAML.";
        LOGGER.warn(message);
        throw new IllegalArgumentException(message);
    }
}
