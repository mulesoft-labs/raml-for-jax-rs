package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.v10.TypeUtils;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/3/17.
 * Just potential zeroes and ones
 */
public class V10GTypeJson extends V10GTypeHelper {

    private final JSONTypeDeclaration typeDeclaration;
    private final String name;
    private final String defaultJavatypeName;

    V10GTypeJson(JSONTypeDeclaration typeDeclaration, String realName, String defaultJavatypeName) {
        super(realName);
        this.typeDeclaration = typeDeclaration;
        this.name = realName;
        this.defaultJavatypeName = defaultJavatypeName;
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

        return true;
    }

    @Override
    public String schema() {

        return typeDeclaration.schemaContent();
    }


    @Override
    public TypeName defaultJavaTypeName(String pack) {

        if ( isInline() ) {
            return ClassName.get("", defaultJavatypeName);
        } else {
            return ClassName.get(pack, defaultJavatypeName);
        }
    }

    public ClassName javaImplementationName(String pack) {

        return null;
    }

    public boolean isInline() {
        return TypeUtils.shouldCreateNewClass(typeDeclaration, typeDeclaration.parentTypes().toArray(new TypeDeclaration[0]));
    }

    @Override
    public String toString() {
        return "V10GTypeJson{" +
                "input=" + typeDeclaration.name() + ":" + typeDeclaration.type()+
                ", name='" + name() + '\'' +
                '}';
    }


    @Override
    public void construct(final CurrentBuild currentBuild, GObjectType objectType) {
        objectType.dispatch(new GObjectType.GObjectTypeDispatcher() {

            @Override
            public void onJsonObject() {

                SchemaTypeFactory.createJsonType(currentBuild, V10GTypeJson.this);
            }
        });
    }


}
