package org.raml.jaxrs.generator;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import joptsimple.internal.Strings;
import org.raml.jaxrs.generator.builders.resources.ResourceBuilder;
import org.raml.jaxrs.generator.builders.resources.ResourceInterface;
import org.raml.jaxrs.generator.builders.types.TypeBuilder;
import org.raml.jaxrs.generator.builders.types.TypeBuilderInterface;
import org.raml.jaxrs.generator.builders.types.TypeDescriber;

import javax.lang.model.element.Modifier;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.raml.jaxrs.generator.Paths.relativize;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * The art of building stuff is here.
 * Factory for building root stuff.
 */
public class CurrentBuild {

    private final String defaultPackage;

    private final List<ResourceBuilder> resources = new ArrayList<ResourceBuilder>();
    private final Map<String, TypeBuilder> types = new HashMap<>();

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public ResourceBuilder createResource(String name, String relativeURI) {

        ResourceBuilder builder = new ResourceInterface(this, name, relativize(relativeURI));
        resources.add(builder);
        return builder;
    }

    public void generate(String rootDirectory) throws IOException {

        ResponseSupport.buildSupportClasses(rootDirectory, this.defaultPackage);
        for (ResourceBuilder resource : resources) {
            resource.output(rootDirectory);
        }

        for (TypeBuilder b: types.values()) {
            b.ouput(rootDirectory);
        }
    }

    public TypeBuilder createType(String name, List<String> parentTypes) {
        TypeBuilder builder = new TypeBuilderInterface(this, name, parentTypes);
        types.put(name, builder);
        return builder;
    }

    public TypeBuilder getDeclaredType(String parentType) {

        return types.get(parentType);
    }

    public void javaTypeName(String type, TypeDescriber describer) {
        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

            describer.asJavaType(scalar);
        } else {

            TypeBuilder builder = types.get(type);
            if ( builder == null ) {

                describer.asBuiltType(type);
            } else {

                throw new IllegalArgumentException("unknown type " + type);
            }
        }


    }
}
