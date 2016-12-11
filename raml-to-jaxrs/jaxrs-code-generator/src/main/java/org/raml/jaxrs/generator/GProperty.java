package org.raml.jaxrs.generator;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public interface GProperty extends GAbstraction{
    String name();
    GType type();

    boolean isInternal();
}
