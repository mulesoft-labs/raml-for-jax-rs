package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.JCodeModel;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.extensions.GsonExtension;
import org.raml.jaxrs.generator.builders.extensions.JacksonExtension;
import org.raml.jaxrs.generator.builders.extensions.JavadocTypeExtension;
import org.raml.jaxrs.generator.builders.extensions.JaxbTypeExtension;
import org.raml.jaxrs.generator.builders.extensions.Jsr303Extension;
import org.raml.jaxrs.generator.builders.extensions.TypeExtension;
import org.raml.jaxrs.generator.builders.extensions.TypeExtensionList;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * The art of building stuff is here.
 * Factory for building root stuff.
 */
public class CurrentBuild {

    private final GFinder typeFinder;
    private final String resourcePackage;
    private final String modelPackage;
    private final String supportPackage;

    private final List<ResourceGenerator> resources = new ArrayList<>();
    private final Map<String, TypeGenerator> builtTypes = new HashMap<>();
    private TypeExtensionList typeExtensionList = new TypeExtensionList();
    private Map<String, GeneratorType> foundTypes = new HashMap<>();
    private final Map<String, JavaPoetTypeGenerator> supportGenerators = new HashMap<>();

    public CurrentBuild(GFinder typeFinder, String resourcePackage, String modelPackage, String supportPackage) {
        this.typeFinder = typeFinder;
        this.resourcePackage = resourcePackage;
        this.modelPackage = modelPackage;
        this.supportPackage = supportPackage;

        typeExtensionList.addExtension(new JaxbTypeExtension());
        typeExtensionList.addExtension(new JavadocTypeExtension());
        typeExtensionList.addExtension(new Jsr303Extension());
        //typeExtensionList.addExtension(new JacksonExtension());
        typeExtensionList.addExtension(new GsonExtension());


    }

    public String getResourcePackage() {
        return resourcePackage;
    }

    public String getModelPackage() {

        return modelPackage;
    }

    public void generate(final String rootDirectory) throws IOException {

        if (resources.size() > 0) {
            ResponseSupport.buildSupportClasses(rootDirectory, getSupportPackage());
        }

        for (TypeGenerator typeGenerator : builtTypes.values()) {

            if (typeGenerator instanceof JavaPoetTypeGenerator) {


                JavaPoetTypeGenerator b = (JavaPoetTypeGenerator) typeGenerator;
                b.output(new CodeContainer<TypeSpec.Builder>() {
                             @Override
                             public void into(TypeSpec.Builder g) throws IOException {

                                 JavaFile.Builder file = JavaFile.builder(getModelPackage(), g.build());
                                 file.build().writeTo(new File(rootDirectory));
                             }
                         }
                );

                continue;
            }

            if (typeGenerator instanceof CodeModelTypeGenerator) {
                CodeModelTypeGenerator b = (CodeModelTypeGenerator) typeGenerator;
                b.output(new CodeContainer<JCodeModel>() {
                    @Override
                    public void into(JCodeModel g) throws IOException {

                        g.build(new File(rootDirectory));
                    }
                });
            }
        }

        for (ResourceGenerator resource : resources) {
            resource.output(new CodeContainer<TypeSpec>() {
                @Override
                public void into(TypeSpec g) throws IOException {
                    JavaFile.Builder file = JavaFile.builder(getResourcePackage(), g);
                    file.build().writeTo(new File(rootDirectory));
                }
            });
        }

        for (JavaPoetTypeGenerator typeGenerator : supportGenerators.values()) {

            typeGenerator.output(new CodeContainer<TypeSpec.Builder>() {
                @Override
                public void into(TypeSpec.Builder g) throws IOException {

                    JavaFile.Builder file = JavaFile.builder(getSupportPackage(), g.build());
                    file.build().writeTo(new File(rootDirectory));
                }
            });
        }
    }


    public TypeExtension withTypeListeners() {

        return typeExtensionList;
    }


    public void newGenerator(String ramlTypeName, TypeGenerator generator) {

        builtTypes.put(ramlTypeName, generator);
    }

    public void newSupportGenerator(String name, JavaPoetTypeGenerator generator) {

        supportGenerators.put(name, generator);
    }

    public GeneratorType getDeclaredType(String ramlType) {

        GeneratorType type = foundTypes.get(ramlType);
        if ( type == null ) {

            throw new GenerationException("no such type " + ramlType);
        }

        return type;
    }

    public TypeName getJavaType(GType type) {

        return getJavaType(type, new HashMap<String, JavaPoetTypeGenerator>(), false);
    }

    public TypeName getJavaType(GType type, Map<String, JavaPoetTypeGenerator> internalTypes) {

        return getJavaType(type, internalTypes, false);
    }


    public TypeName getJavaType(GType type, Map<String, JavaPoetTypeGenerator> internalTypes, boolean useName) {

        TypeName name = checkJavaType(type, internalTypes, useName);
        if ( name == null ) {
            throw new GenerationException("unknown type " + type.type() + "(" + type.name() + ")");
        }
        return name;
    }

    private TypeName checkJavaType(GType type, Map<String, JavaPoetTypeGenerator> internalTypes, boolean useName) {

        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

            return classToTypeName(scalar);
        } else {

            if (type.isArray()) {

                TypeName contained = getJavaType(type.arrayContents(), internalTypes, true);
                return ParameterizedTypeName.get(ClassName.get("java.util", "List"), contained);
            } else {

                TypeGenerator builder = internalTypes.get(type.name());

                if (builder == null) {
                    // it's not an internal type.  It's a global type.
                    if ( builtTypes.get(type.name()) != null ) {
                        // it's a build type.  We have a new class for this.
                        return builtTypes.get(type.name()).getGeneratedJavaType();
                    } else {
                        // it's an extension of an existing class, but a new type nontheless.
                        GeneratorType gen = foundTypes.get(type.name());
                        return ClassName.get(getModelPackage(), gen.getDeclaredType().defaultJavaTypeName());
                    }
                }
                // it was an internal class that we built....
                return builder.getGeneratedJavaType();
            }
        }
    }

    public TypeName classToTypeName(Class<?> scalar) {
        if ( scalar.isPrimitive()) {
            switch(scalar.getSimpleName()) {
                case "int":
                    return TypeName.INT;

                case "boolean":
                    return TypeName.BOOLEAN;

                case "double":
                    return TypeName.DOUBLE;

                case "float":
                    return TypeName.FLOAT;

                case "byte":
                    return TypeName.BYTE;

                default:
                    throw new GenerationException("JP, finish the list " + scalar);
            }
        } else {
            return ClassName.get(scalar);
        }
    }

    public void newResource(ResourceGenerator rg) {

        resources.add(rg);
    }

    public void constructClasses(TypeFactory typeFactory) {

        typeFinder.findTypes(new TypeFindingListener(foundTypes));

        for (GeneratorType type : foundTypes.values()) {

            if ( type.getObjectType() != GObjectType.SCALAR) {
                typeFactory.createType(type);
            }
        }
    }

    public String getSupportPackage() {
        return supportPackage;
    }
}

