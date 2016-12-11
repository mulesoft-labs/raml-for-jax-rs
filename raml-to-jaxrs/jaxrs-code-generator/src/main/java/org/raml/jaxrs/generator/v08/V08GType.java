package org.raml.jaxrs.generator.v08;

import org.raml.jaxrs.generator.GProperty;
import org.raml.jaxrs.generator.GType;
import org.raml.v2.api.model.v08.api.GlobalSchema;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.resources.Resource;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V08GType implements GType {


    public V08GType(Resource resource, Method method, BodyLike typeDeclaration) {

    }

    public V08GType(Resource resource, Method method, Response response, BodyLike typeDeclaration) {

    }

    public V08GType(GlobalSchema schema) {

    }

    @Override
    public Object implementation() {
        return null;
    }

    @Override
    public String type() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public boolean isJson() {
        return false;
    }

    @Override
    public boolean isXml() {
        return false;
    }

    @Override
    public String schema() {
        return null;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public List<GType> parentTypes() {
        return null;
    }

    @Override
    public boolean declaresProperty(String name) {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public List<GProperty> properties() {
        return null;
    }

    @Override
    public GType arrayContents() {
        return null;
    }

    @Override
    public String defaultJavaTypeName() {
        return null;
    }
}
