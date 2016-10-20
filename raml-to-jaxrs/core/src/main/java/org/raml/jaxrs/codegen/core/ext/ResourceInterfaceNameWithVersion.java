package org.raml.jaxrs.codegen.core.ext;

import org.aml.apimodel.Resource;
import org.raml.jaxrs.codegen.core.Configuration;
import org.raml.jaxrs.codegen.core.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>ResourceInterfaceNameWithVersion class.</p>
 *
 * @author Pavel
 * @version $Id: $Id
 */
public class ResourceInterfaceNameWithVersion extends AbstractGeneratorExtension implements InterfaceNameBuilderExtension {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceInterfaceNameWithVersion.class);

    /** {@inheritDoc} */
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
