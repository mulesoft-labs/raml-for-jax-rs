package org.raml.jaxrs.generator;

import org.raml.v2.api.model.v10.resources.Resource;

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
