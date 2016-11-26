package org.raml.jaxrs.generator.builders.resources;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.builders.Generator;
import org.raml.jaxrs.generator.builders.types.RamlTypeGenerator;

import java.io.IOException;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
public interface ResourceGenerator extends Generator<TypeSpec> {

    ResourceGenerator withDocumentation(String docs);
    ResourceGenerator mediaType(List<String> mimeTypes);

    ResponseClassBuilder createResponseClassBuilder(String method, String additionalNames);

    MethodBuilder createMethod(String method, String fullMethodName, String returnClass);

    void addInternalType(RamlTypeGenerator internalGenerator);
}
