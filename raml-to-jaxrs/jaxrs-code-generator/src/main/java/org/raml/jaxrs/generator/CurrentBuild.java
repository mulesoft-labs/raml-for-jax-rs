package org.raml.jaxrs.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.JCodeModel;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.extensions.types.TypeExtension;
import org.raml.jaxrs.generator.builders.extensions.types.TypeExtensionList;
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

    private final List<JavaPoetTypeGenerator> supportGenerators = new ArrayList<>();

    public CurrentBuild(GFinder typeFinder, String resourcePackage, String modelPackage, String supportPackage) {

        this.typeFinder = typeFinder;
        this.resourcePackage = resourcePackage;
        this.modelPackage = modelPackage;
        this.supportPackage = supportPackage;
    }

    public void addExtension(TypeExtension extension) {

        typeExtensionList.addExtension(extension);
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

        for (JavaPoetTypeGenerator typeGenerator : supportGenerators) {

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

    public void newSupportGenerator(JavaPoetTypeGenerator generator) {

        supportGenerators.add(generator);
    }

    public <T extends TypeGenerator> T getBuiltType(String ramlType) {

        TypeGenerator type = builtTypes.get(ramlType);
        if ( type == null ) {

            throw new GenerationException("no such type " + ramlType);
        }

        return (T) type;
    }

    public void newResource(ResourceGenerator rg) {

        resources.add(rg);
    }

    public void constructClasses() {

        TypeFindingListener listener = new TypeFindingListener(foundTypes);
        typeFinder.findTypes(listener);

        for (GeneratorType type : foundTypes.values()) {

            type.construct(this);
        }
    }


    public String getSupportPackage() {
        return supportPackage;
    }

}

