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
import org.raml.jaxrs.generator.builders.extensions.JavadocTypeExtension;
import org.raml.jaxrs.generator.builders.extensions.JaxbTypeExtension;
import org.raml.jaxrs.generator.builders.extensions.TypeExtension;
import org.raml.jaxrs.generator.builders.extensions.TypeExtensionList;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;
import org.raml.jaxrs.generator.builders.TypeDescriber;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

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

    private final String defaultPackage;

    private final List<ResourceGenerator> resources = new ArrayList<>();
    private final Map<String, TypeGenerator> types = new HashMap<>();
    private final Map<String, CodeModelTypeGenerator> codeModelTypes = new HashMap<>();
    private final Map<String, JavaPoetTypeGenerator> javaPoetTypes = new HashMap<>();
    private TypeExtensionList typeExtensionList = new TypeExtensionList();
    private String modelPackage;

    public CurrentBuild(String defaultPackage) {

        this.defaultPackage = defaultPackage;
        typeExtensionList.addExtension(new JaxbTypeExtension());
        typeExtensionList.addExtension(new JavadocTypeExtension());

    }

    public String getDefaultPackage() {
        return defaultPackage;
    }

    public String getModelPackage() {

        return defaultPackage;
    }


    public void generate(final String rootDirectory) throws IOException {


        if ( resources.size() > 0 ) {
            ResponseSupport.buildSupportClasses(rootDirectory, this.defaultPackage);
        }

        for (TypeGenerator typeGenerator : types.values()) {

            if ( typeGenerator instanceof JavaPoetTypeGenerator ) {


                JavaPoetTypeGenerator b = (JavaPoetTypeGenerator) typeGenerator;
                b.output(new CodeContainer<TypeSpec.Builder>() {
                    @Override
                    public void into(TypeSpec.Builder g) throws IOException {

                        JavaFile.Builder file = JavaFile.builder(getDefaultPackage(), g.build());
                        file.build().writeTo(new File(rootDirectory));
                    }
                }
                );

                continue;
            }

            if ( typeGenerator instanceof  CodeModelTypeGenerator ) {
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
                    JavaFile.Builder file = JavaFile.builder(getDefaultPackage(), g);
                    file.build().writeTo(new File(rootDirectory));
                }
            });
        }

    }

    public void javaTypeName(TypeDeclaration type, TypeDescriber describer) {
        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

            describer.asJavaType(this, scalar);
        } else {

            TypeGenerator builder = types.get(type);
            if ( builder != null ) {

                describer.asBuiltType(this, builder.getGeneratedJavaType());
            } else {

                throw new IllegalArgumentException("unknown type " + type);
            }
        }


    }

    public TypeExtension withTypeListeners() {

        return typeExtensionList;
    }


    public void newKnownType(String ramlTypeName, TypeGenerator generator) {

        types.put(ramlTypeName, generator);
    }

    public TypeGenerator getDeclaredType(String ramlType) {

        return types.get(ramlType);
    }

    public TypeName getJavaType(TypeDeclaration type) {

        return getJavaType(type, new HashMap<String, JavaPoetTypeGenerator>());
    }

    public TypeName getJavaType(TypeDeclaration type, Map<String, JavaPoetTypeGenerator> internalTypes) {


        TypeName name = checkJavaType(type, internalTypes);
        if ( name == null ) {
            throw new GenerationException("unknown type " + type.type() + "(" + type.name() + ")");
        }
        return name;
    }

    public TypeName checkJavaType(TypeDeclaration type, Map<String, JavaPoetTypeGenerator> internalTypes) {

        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

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

                    default:
                        throw new GenerationException("JP, finish the list " + scalar);
                }
            } else {
                return ClassName.get(scalar);
            }
        } else {

            if ( type instanceof ArrayTypeDeclaration ) {

                ArrayTypeDeclaration listType = (ArrayTypeDeclaration) type;
                TypeName contained = getJavaType(listType.items(), internalTypes);
                return ParameterizedTypeName.get(ClassName.get("java.util", "List"), contained);

            } else {

                TypeGenerator builder = internalTypes.get(type.name());
                if ( builder == null ) {
                    builder = types.get(getTypeName(type));
                }

                if ( builder != null ) {

                    return builder.getGeneratedJavaType();
                } else {

                    return null;
                }
            }
        }
    }

    private String getTypeName(TypeDeclaration type) {

        if ( type.name().contains("/") ) { // horrible hack.

            return type.type();
        } else {

            return type.name();
        }
    }

    public void newResource(ResourceGenerator rg) {

        resources.add(rg);
    }
}

