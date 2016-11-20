package org.raml.jaxrs.generator.builders.resources;

import org.raml.jaxrs.generator.builders.Generator;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
public interface ResourceGenerator extends Generator {

    ResourceGenerator withDocumentation(String docs);
    ResourceGenerator mediaType(List<String> mimeTypes);

    ResponseClassBuilder createResponseClassBuilder(String method, String additionalNames);

    void output(String rootDirectory) throws IOException;

    MethodBuilder createMethod(String method, String fullMethodName, String returnClass);
}
