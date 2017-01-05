package org.raml.jaxrs.generator;


import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public interface GMethod extends GAbstraction{
    List<GRequest> body();
    GResource resource();
    String method();
    List<GParameter> queryParameters();
    List<GResponse> responses();
}
