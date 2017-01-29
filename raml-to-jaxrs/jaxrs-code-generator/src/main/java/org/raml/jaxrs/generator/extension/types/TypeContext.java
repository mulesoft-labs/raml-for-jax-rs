package org.raml.jaxrs.generator.extension.types;

import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.v10.V10GType;

/**
 * Created by Jean-Philippe Belanger on 1/29/17.
 * Just potential zeroes and ones
 */
public interface TypeContext {
    void addImplementation();
    void createInternalClass(JavaPoetTypeGenerator internalGenerator);
}
