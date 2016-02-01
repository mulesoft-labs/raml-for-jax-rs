package org.raml.jaxrs.codegen.core.ext;

import org.raml.jaxrs.codegen.core.Configuration;
import org.raml.jaxrs.codegen.core.Names;
import org.raml.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.regex.Pattern;

public class ResourceInterfaceNameWithVersion extends AbstractGeneratorExtension implements InterfaceNameBuilderExtension {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceInterfaceNameWithVersion.class);
    private static final Pattern VERSION_PATTERN = Pattern.compile("^v[0-9]+$");

    @Override
    public String buildResourceInterfaceName(Resource resource) {
        Configuration config = new Configuration();
        config.setInterfaceNameSuffix("Resource");
        String resourceName = Names.buildResourceInterfaceName(resource, config);
        Optional<String> version = Optional.ofNullable(getRaml().getVersion());
        if (version.isPresent() && VERSION_PATTERN.matcher(version.get()).matches()) {
            resourceName += version.get().toUpperCase();
            LOGGER.debug("Generated resource name: " + resourceName);
            return resourceName;
        }
        String message = "Invalid version found in RAML: " + version;
        LOGGER.warn(message);
        throw new IllegalArgumentException(message);
    }
}
