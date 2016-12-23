package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.GProperty;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.Names;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public class V10GType implements GType {

    private final Resource resource;
    private final Method method;
    private final Response response;
    private final TypeDeclaration typeDeclaration;
    private final String name;
    private final String defaultJavatypeName;

    private List<GProperty> properties;

    public V10GType(Resource resource, Method method, TypeDeclaration typeDeclaration) {

        this.resource = resource;
        this.method = method;
        this.response = null;
        this.typeDeclaration = typeDeclaration;

        if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

            getProperties((ObjectTypeDeclaration)typeDeclaration);
        } else {

            properties = new ArrayList<>();
        }

        this.name = Names.ramlTypeName(resource, method, typeDeclaration);
        this.defaultJavatypeName = Names.javaTypeName(resource, method, typeDeclaration);
    }

    public V10GType(Resource resource, Method method, Response response, TypeDeclaration typeDeclaration) {

        this.resource = resource;
        this.method = method;
        this.response = response;
        this.typeDeclaration = typeDeclaration;
        this.name = Names.ramlTypeName(resource, method, typeDeclaration);
        this.defaultJavatypeName = Names.javaTypeName(resource, method, response, typeDeclaration);

        if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

            getProperties((ObjectTypeDeclaration)typeDeclaration);
        } else {

            properties = new ArrayList<>();
        }
    }

    public V10GType(TypeDeclaration typeDeclaration) {

        this.resource = null;
        this.method = null;
        this.response = null;
        this.typeDeclaration = typeDeclaration;
        this.name = typeDeclaration.name();
        this.defaultJavatypeName = Names.typeName(typeDeclaration.name());
        if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

            getProperties((ObjectTypeDeclaration)typeDeclaration);
        } else {

            properties = new ArrayList<>();
        }
    }

    public V10GType(String s, TypeDeclaration items) {
        this.resource = null;
        this.method = null;
        this.response = null;
        this.name = s;
        this.typeDeclaration = items;
        this.defaultJavatypeName = Names.typeName(typeDeclaration.name());
        if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

            getProperties((ObjectTypeDeclaration)typeDeclaration);
        } else {

            properties = new ArrayList<>();
        }
    }



    private void getProperties(final ObjectTypeDeclaration input) {

        properties = Lists.transform(input.properties(), new Function<TypeDeclaration, GProperty>() {
            @Nullable
            @Override
            public GProperty apply(@Nullable TypeDeclaration declaration) {

                if ( TypeUtils.isNewTypeDeclaration(declaration)) {

                    return new V10GProperty(declaration, new V10GType(declaration.type(),  declaration));
                } else {

                    return new V10GProperty(declaration, new V10GType(declaration.type(),  declaration));
                }
            }
        });
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

        return typeDeclaration instanceof JSONTypeDeclaration;
    }

    @Override
    public boolean isXml() {
        return typeDeclaration instanceof XMLTypeDeclaration;
    }

    @Override
    public boolean isObject() {
        return typeDeclaration instanceof ObjectTypeDeclaration;
    }

    @Override
    public String schema() {
        if ( typeDeclaration instanceof XMLTypeDeclaration ) {
            return ((XMLTypeDeclaration) typeDeclaration).schemaContent();
        }

        if ( typeDeclaration instanceof JSONTypeDeclaration) {

            return ((JSONTypeDeclaration) typeDeclaration).schemaContent();
        }

        throw new GenerationException("type " + this + " has no schema");
    }

    @Override
    public List<GType> parentTypes() {
        return new ArrayList<>();
    }

    @Override
    public List<GProperty> properties() {

        return properties;
    }

    @Override
    public boolean declaresProperty(String name) {

        if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

            for (TypeDeclaration typeDeclaration : ((ObjectTypeDeclaration) typeDeclaration).properties()) {
                if ( typeDeclaration.name().equals(name)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }


    @Override
    public boolean isArray() {
        return typeDeclaration instanceof ArrayTypeDeclaration;
    }

    public boolean isScalar() {

        return ! isArray() && !isXml() && !isJson() && !isObject();
    }

    @Override
    public GType arrayContents() {

        ArrayTypeDeclaration d = (ArrayTypeDeclaration) typeDeclaration;
        return new V10GType(d.items().name().replaceAll("\\[\\]", ""),  d.items());
    }

    @Override
    public String defaultJavaTypeName() {

        return defaultJavatypeName;
    }

    @Override
    public boolean isEnum() {
        return  typeDeclaration instanceof StringTypeDeclaration && ((StringTypeDeclaration) typeDeclaration).enumValues().size() > 0;
    }

    @Override
    public List<String> enumValues() {
        return ((StringTypeDeclaration)typeDeclaration).enumValues();
    }

    @Override
    public String toString() {
        return "V10GType{" +
                "input=" + typeDeclaration.name() + ":" + typeDeclaration.type()+
                ", name='" + name() + '\'' +
                '}';
    }


}
