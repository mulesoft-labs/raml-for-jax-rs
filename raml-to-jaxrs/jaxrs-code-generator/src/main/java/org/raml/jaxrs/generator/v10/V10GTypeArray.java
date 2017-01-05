package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.v10.V10GProperty;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/5/17.
 * Just potential zeroes and ones
 */
public class V10GTypeArray implements V10GType {
    private final V10TypeRegistry registry;
    private final String name;
    private final ArrayTypeDeclaration typeDeclaration;

    public V10GTypeArray(V10TypeRegistry registry, String name, ArrayTypeDeclaration typeDeclaration) {
        this.registry = registry;
        this.name = name;
        this.typeDeclaration = typeDeclaration;
    }

    @Override
    public TypeDeclaration implementation() {
        return typeDeclaration;
    }

    @Override
    public String type() {
        return typeDeclaration.type();
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isJson() {
        return false;
    }

    @Override
    public boolean isUnion() {
        return false;
    }

    @Override
    public boolean isXml() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public String schema() {
        return null;
    }

    @Override
    public List<V10GType> parentTypes() {
        return Collections.emptyList();
    }

    @Override
    public List<V10GProperty> properties() {
        return Collections.emptyList();
    }

    @Override
    public boolean isArray() {
        return true;
    }

    @Override
    public GType arrayContents() {
        return registry.fetchType(typeDeclaration.items().name(), typeDeclaration.items());
    }

    @Override
    public TypeName defaultJavaTypeName(String pack) {
        return ParameterizedTypeName.get(
                ClassName.get(List.class),
                arrayContents().defaultJavaTypeName(pack));
    }

    @Override
    public ClassName javaImplementationName(String pack) {
        return null;
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public List<String> enumValues() {
        return null;
    }

    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public Collection<V10GType> childClasses(String typeName) {
        return Collections.emptyList();
    }

    @Override
    public void construct(CurrentBuild currentBuild, GObjectType objectType) {

    }
}
