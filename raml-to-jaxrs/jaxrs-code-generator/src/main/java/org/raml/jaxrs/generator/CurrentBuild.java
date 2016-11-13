package org.raml.jaxrs.generator;

import org.raml.jaxrs.generator.builders.resources.ResourceBuilder;
import org.raml.jaxrs.generator.builders.resources.ResourceInterface;
import org.raml.jaxrs.generator.builders.types.CompositeTypeBuilder;
import org.raml.jaxrs.generator.builders.types.TypeBuilder;
import org.raml.jaxrs.generator.builders.types.TypeBuilderImplementation;
import org.raml.jaxrs.generator.builders.types.TypeBuilderInterface;
import org.raml.jaxrs.generator.builders.TypeDescriber;

import java.io.IOException;
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
        TypeBuilderInterface intf = new TypeBuilderInterface(this, name, parentTypes);
        TypeBuilderImplementation impl = new TypeBuilderImplementation(this, name, name);

        CompositeTypeBuilder compositeTypeBuilder = new CompositeTypeBuilder(intf, impl);
        types.put(name, compositeTypeBuilder);
        return compositeTypeBuilder;
    }

    public TypeBuilder getDeclaredType(String parentType) {

        return types.get(parentType);
    }

    public void javaTypeName(String type, TypeDescriber describer) {
        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

            describer.asJavaType(this, scalar);
        } else {

            TypeBuilder builder = types.get(type);
            if ( builder != null ) {

                describer.asBuiltType(this, type);
            } else {

                throw new IllegalArgumentException("unknown type " + type);
            }
        }


    }
}
