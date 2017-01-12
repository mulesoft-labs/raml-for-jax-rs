package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.ramltypes.GType;

/**
 * Created by Jean-Philippe Belanger on 12/7/16.
 * Just potential zeroes and ones
 */
public interface GFinderListener {

    void newTypeDeclaration(GType type);
}
