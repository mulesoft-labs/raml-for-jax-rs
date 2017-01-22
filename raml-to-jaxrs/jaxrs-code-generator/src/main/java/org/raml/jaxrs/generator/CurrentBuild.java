package org.raml.jaxrs.generator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.GenerationConfig;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.CodeModelTypeGenerator;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.extensions.types.GsonExtension;
import org.raml.jaxrs.generator.builders.extensions.types.JacksonExtensions;
import org.raml.jaxrs.generator.builders.extensions.types.JavadocTypeExtension;
import org.raml.jaxrs.generator.builders.extensions.types.JaxbTypeExtension;
import org.raml.jaxrs.generator.builders.extensions.types.Jsr303Extension;
import org.raml.jaxrs.generator.builders.extensions.types.TypeExtensionList;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.v2.api.model.v10.api.Api;

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
    private final Api api;

    private final List<ResourceGenerator> resources = new ArrayList<>();
    private final Map<String, TypeGenerator> builtTypes = new HashMap<>();
    private TypeExtensionList typeExtensionList = new TypeExtensionList();
    private Map<String, GeneratorType> foundTypes = new HashMap<>();

    private final List<JavaPoetTypeGenerator> supportGenerators = new ArrayList<>();
    private Configuration configuration;

    public CurrentBuild(GFinder typeFinder, Api api) {

        this.typeFinder = typeFinder;
        this.api = api;
        this.configuration = Configuration.defaultConfiguration();
    }


    public Api getApi() {
        return api;
    }

    public String getResourcePackage() {

        return configuration.getResourcePackage();
    }

    public String getModelPackage() {

        return configuration.getModelPackage();
    }

    public String getSupportPackage() {
        return configuration.getSupportPackage();
    }

    public void generate(final File rootDirectory) throws IOException {

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
                                 file.build().writeTo(rootDirectory);
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

                        g.build(rootDirectory);
                    }
                });
            }
        }

        for (ResourceGenerator resource : resources) {
            resource.output(new CodeContainer<TypeSpec>() {
                @Override
                public void into(TypeSpec g) throws IOException {
                    JavaFile.Builder file = JavaFile.builder(getResourcePackage(), g);
                    file.build().writeTo(rootDirectory);
                }
            });
        }

        for (JavaPoetTypeGenerator typeGenerator : supportGenerators) {

            typeGenerator.output(new CodeContainer<TypeSpec.Builder>() {
                @Override
                public void into(TypeSpec.Builder g) throws IOException {

                    JavaFile.Builder file = JavaFile.builder(getSupportPackage(), g.build());
                    file.build().writeTo(rootDirectory);
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

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;

        for (String s : this.configuration.getTypeConfiguration()){

            if ( s.equals("jackson") ) {
                typeExtensionList.addExtension(new JacksonExtensions());
            }

            if ( s.equals("jaxb") ) {

                typeExtensionList.addExtension(new JaxbTypeExtension());
            }

            if ( s.equals("gson") ) {

                typeExtensionList.addExtension(new GsonExtension());
            }

            if ( s.equals("javadoc") ) {

                typeExtensionList.addExtension(new JavadocTypeExtension());
            }

            if ( s.equals("jsr303") ) {

                typeExtensionList.addExtension(new Jsr303Extension());
            }
        }
    }

    public GenerationConfig getJsonMapperConfig() {
        return configuration.createJsonSchemaGenerationConfig();
    }
}

