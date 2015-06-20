package org.raml.jaxrs.codegen.core.ext;

import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Resource;

public interface MethodNameBuilderExtension extends GeneratorExtension {

	String buildResourceMethodName(final Action action, final MimeType bodyMimeType, Resource resource);
}
