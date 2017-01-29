package org.raml.jaxrs.generator.builders.extensions.types;

import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.extensions.ContextImpl;
import org.raml.jaxrs.generator.extension.types.TypeContext;
import org.raml.jaxrs.generator.v10.V10GType;

/**
 * Created by Jean-Philippe Belanger on 1/29/17.
 * Just potential zeroes and ones
 */
abstract public class TypeContextImpl extends ContextImpl implements TypeContext {


    public TypeContextImpl(CurrentBuild build) {
        super(build);
    }
}
