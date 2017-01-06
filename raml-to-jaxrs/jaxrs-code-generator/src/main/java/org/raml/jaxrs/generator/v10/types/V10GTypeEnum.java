package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.v10.TypeUtils;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/5/17.
 * Just potential zeroes and ones
 */
public class V10GTypeEnum extends V10GTypeHelper {


    private final V10TypeRegistry v10TypeRegistry;
    private final String name;
    private final String javaTypeName;
    private final StringTypeDeclaration typeDeclaration;

    public V10GTypeEnum(V10TypeRegistry v10TypeRegistry, String name, String javaTypeName, StringTypeDeclaration typeDeclaration) {
        super(name);

        this.v10TypeRegistry = v10TypeRegistry;
        this.name = name;
        this.javaTypeName = javaTypeName;
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
    public TypeName defaultJavaTypeName(String pack) {

        if ( javaTypeName.contains(".")) {
            return ClassName.bestGuess(javaTypeName);
        } else {
            if ( isInline() ) {
                return ClassName.get("", javaTypeName);
            } else {
                return ClassName.get(pack, javaTypeName);
            }
        }
    }

    @Override
    public ClassName javaImplementationName(String pack) {
        return null;
    }

    @Override
    public boolean isEnum() {
        return true;
    }

    public boolean isInline() {
        return TypeUtils.shouldCreateNewClass(typeDeclaration, typeDeclaration.parentTypes().toArray(new TypeDeclaration[0]));
    }

    @Override
    public List<String> enumValues() {
        return typeDeclaration.enumValues();
    }

    @Override
    public void construct(final CurrentBuild currentBuild, GObjectType objectType) {
        objectType.dispatch(new GObjectType.GObjectTypeDispatcher() {

            @Override
            public void onEnumeration() {

                V10TypeFactory.createEnumerationType(currentBuild, V10GTypeEnum.this);
            }
        });

    }
}
