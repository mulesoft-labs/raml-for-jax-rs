package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.GProperty;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public class V10GType implements GType {

    private final TypeDeclaration input;
    private final String[] nameComponents;
    private List<GProperty> properties;

    public V10GType(TypeDeclaration input){

        this.nameComponents = new String[] {input.name()};
        this.input = input;
    }

    public V10GType(String type, TypeDeclaration input){

        this.nameComponents = new String[] {type};
        this.input = input;
    }

    public V10GType(String resource, String method, String response, String type, TypeDeclaration input) {

        this.nameComponents = new String[] {resource, method, response, type};
        this.input = input;

        if ( input instanceof ObjectTypeDeclaration ) {

            getProperties((ObjectTypeDeclaration)input);
        }
    }

    public V10GType(String resource, String method, String type, TypeDeclaration input) {

        this.nameComponents = new String[] {resource, method, type};
        this.input = input;

        if ( input instanceof ObjectTypeDeclaration ) {

            getProperties((ObjectTypeDeclaration)input);
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
        return input;
    }

    @Override
    public String type() {
        return input.type();
    }

    @Override
    public String name() {
        return Names.ramlTypeName();
    }

    @Override
    public boolean isJson() {

        return input instanceof JSONTypeDeclaration;
    }

    @Override
    public boolean isXml() {
        return input instanceof XMLTypeDeclaration;
    }

    @Override
    public boolean isObject() {
        return input instanceof ObjectTypeDeclaration;
    }

    @Override
    public String schema() {
        if ( input instanceof XMLTypeDeclaration ) {
            return ((XMLTypeDeclaration) input).schemaContent();
        }

        if ( input instanceof JSONTypeDeclaration) {

            return ((JSONTypeDeclaration) input).schemaContent();
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

        if ( input instanceof ObjectTypeDeclaration ) {

            for (TypeDeclaration typeDeclaration : ((ObjectTypeDeclaration) input).properties()) {
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
        return input instanceof ArrayTypeDeclaration;
    }

    public boolean isScalar() {

        return ! isArray() && !isXml() && !isJson() && !isObject();
    }

    @Override
    public GType arrayContents() {

        ArrayTypeDeclaration d = (ArrayTypeDeclaration) input;
        return new V10GType(d.items().name().replaceAll("\\[\\]", ""),  d.items());
    }

    @Override
    public String toString() {
        return "V10GType{" +
                ", input=" + input.name() + ":" + input.type()+
                ", name='" + name + '\'' +
                '}';
    }
}
