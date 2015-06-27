package org.raml.jaxrs.codegen.core.ext;

import org.raml.model.Resource;

public interface InterfaceNameBuilderExtension extends GeneratorExtension {
	
	/**
	 * Compose name of JAX RS interface
	 * @param resource RAML resource
	 * @return JAX RS interface name
	 */
	String buildResourceInterfaceName(final Resource resource);

}
