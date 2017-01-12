package org.raml.jaxrs.generator.ramltypes;

import org.raml.jaxrs.generator.GAbstraction;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public interface GResource extends GAbstraction {


    List<GResource> resources();

    List<GMethod> methods();

    List<GParameter> uriParameters();

    String resourcePath();

    GResource parentResource();
}
