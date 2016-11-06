package org.raml.jaxrs.model;

public interface PathFragment {
    /**
     * @return The string representation of the fragment. In the case of a plain string, returns the
     *  valid URL string corresponding to it. In the case of a path parameter, this call returns
     *  the parameter name surrounded in curly braces.
     */
    String getStringRepresentation();
}
