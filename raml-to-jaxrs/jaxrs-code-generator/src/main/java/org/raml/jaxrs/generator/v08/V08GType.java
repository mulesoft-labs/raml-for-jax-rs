package org.raml.jaxrs.generator.v08;

import org.raml.jaxrs.generator.GProperty;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.Names;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.resources.Resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V08GType implements GType {


    private final String ramlName;
    private final String defaultJavaName;
    private final BodyLike typeDeclaration;

    public V08GType(Resource resource, Method method, BodyLike typeDeclaration) {

        this.ramlName = Names.ramlTypeName(resource, method, typeDeclaration);
        this.defaultJavaName = Names.javaTypeName(resource, method, typeDeclaration);
        this.typeDeclaration = typeDeclaration;
    }

    public V08GType(Resource resource, Method method, Response response, BodyLike typeDeclaration) {

        this.ramlName = Names.ramlTypeName(resource, method, response, typeDeclaration);
        this.defaultJavaName = Names.javaTypeName(resource, method, response, typeDeclaration);
        this.typeDeclaration = typeDeclaration;
    }

    public V08GType(String type) {
        this.ramlName = type;
        this.typeDeclaration = null;
        this.defaultJavaName = Names.typeName(type);
    }

    public V08GType(String type, BodyLike typeDeclaration) {
        this.ramlName = type;
        this.typeDeclaration = typeDeclaration;
        this.defaultJavaName = Names.typeName(type);
    }

    @Override
    public BodyLike implementation() {
        return typeDeclaration;
    }

    @Override
    public String type() {
        return ramlName;
    }

    @Override
    public String name() {
        return ramlName;
    }

    @Override
    public boolean isJson() {
        return typeDeclaration != null && typeDeclaration.name().equals("application/json");
    }

    @Override
    public boolean isXml() {
        return typeDeclaration != null && typeDeclaration.name().equals("application/xml");
    }

    @Override
    public String schema() {
        return typeDeclaration.schemaContent();
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public List<GType> parentTypes() {
        return new ArrayList<>();
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
        return new ArrayList<>();
    }

    @Override
    public GType arrayContents() {
        return null;
    }

    @Override
    public String defaultJavaTypeName() {
        return defaultJavaName;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public List<String> enumValues() {
        return Collections.emptyList();
    }
}
