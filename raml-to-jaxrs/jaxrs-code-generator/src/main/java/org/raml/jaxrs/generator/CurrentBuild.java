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
import org.raml.jaxrs.generator.v10.TypeFactory;
import org.raml.jaxrs.generator.v10.V10TypeFinder;
import org.raml.jaxrs.generator.v10.V10GeneratorContext;
import org.raml.jaxrs.generator.v10.V10TypeFinderListener;
import org.raml.v2.api.model.v10.api.Api;
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

    private final V10TypeFinder typeFinder;
    private final String resourcePackage;
    private final String modelPackage;

    private final List<ResourceGenerator> resources = new ArrayList<>();
    private final Map<String, TypeGenerator> builtTypes = new HashMap<>();
    private TypeExtensionList typeExtensionList = new TypeExtensionList();
    private Map<String, GeneratorType<V10GeneratorContext>> foundTypes = new HashMap<>();

    public CurrentBuild(V10TypeFinder typeFinder, String resourcePackage, String modelPackage) {
        this.typeFinder = typeFinder;
        this.resourcePackage = resourcePackage;
        this.modelPackage = modelPackage;

        typeExtensionList.addExtension(new JaxbTypeExtension());
        typeExtensionList.addExtension(new JavadocTypeExtension());
    }

    public String getResourcePackage() {
        return resourcePackage;
    }

    public String getModelPackage() {

        return modelPackage;
    }

    public void generate(final String rootDirectory) throws IOException {

        if ( resources.size() > 0 ) {
            ResponseSupport.buildSupportClasses(rootDirectory, getResourcePackage());
        }

        for (TypeGenerator typeGenerator : builtTypes.values()) {

            if ( typeGenerator instanceof JavaPoetTypeGenerator ) {


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
                    JavaFile.Builder file = JavaFile.builder(getResourcePackage(), g);
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

    public GeneratorType<?> getDeclaredType(String ramlType) {

        GeneratorType<V10GeneratorContext> type = foundTypes.get(ramlType);
        if ( type == null ) {

            throw new GenerationException("no such type " + ramlType);
        }

        return type;
    }

    public TypeName getJavaType(TypeDeclaration type) {

        return getJavaType(type, new HashMap<String, JavaPoetTypeGenerator>(), false);
    }

    public TypeName getJavaType(TypeDeclaration type, boolean useName) {

        return getJavaType(type, new HashMap<String, JavaPoetTypeGenerator>(), useName);
    }


    public TypeName getJavaType(TypeDeclaration type, Map<String, JavaPoetTypeGenerator> internalTypes, boolean useName) {

        TypeName name = checkJavaType(type, internalTypes, useName);
        if ( name == null ) {
            throw new GenerationException("unknown type " + type.type() + "(" + type.name() + ")");
        }
        return name;
    }

    private TypeName checkJavaType(TypeDeclaration type, Map<String, JavaPoetTypeGenerator> internalTypes, boolean useName) {

        Class<?> scalar = ScalarTypes.scalarToJavaType(type);
        if ( scalar != null ){

            return classToTypeName(scalar);
        } else {

            if ( type instanceof ArrayTypeDeclaration ) {

                ArrayTypeDeclaration listType = (ArrayTypeDeclaration) type;
                TypeName contained = getJavaType(listType.items(), internalTypes, true);
                return ParameterizedTypeName.get(ClassName.get("java.util", "List"), contained);

            } else {

                TypeGenerator builder = internalTypes.get(type.name());
                if ( builder == null ) {
                    GeneratorType gen = foundTypes.get(getTypeName(type, useName));
                    return ClassName.get(getModelPackage(), gen.getJavaTypeName());
                }

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

                default:
                    throw new GenerationException("JP, finish the list " + scalar);
            }
        } else {
            return ClassName.get(scalar);
        }
    }

    public boolean isBuilt(String name) {

        return builtTypes.containsKey(name);
    }

    private String getTypeName(TypeDeclaration type, boolean useName) {

        if (!useName) { // horrible hack.

            return type.type();
        } else {

            return type.name();
        }
    }

    public void newResource(ResourceGenerator rg) {

        resources.add(rg);
    }

    public void constructClasses(Api api, TypeFactory typeFactory) {

        typeFinder.findTypes(new V10TypeFinderListener(foundTypes));
        for (GeneratorType<V10GeneratorContext> type : foundTypes.values()) {

            V10GeneratorContext context = type.getContext();
            if ( context.getResponse() != null ) {

                typeFactory.createPrivateTypeForResponse(api, context.getResource(), context.getMethod(), context.getResponse(), context.getTypeDeclaration());
                continue;
            }

            if ( context.getMethod() != null ) {
                typeFactory.createPrivateTypeForRequest(api, context.getResource(), context.getMethod(), context.getTypeDeclaration());
            } else {

                typeFactory.createType(api, context.getTypeDeclaration());
            }
        }
    }

}

