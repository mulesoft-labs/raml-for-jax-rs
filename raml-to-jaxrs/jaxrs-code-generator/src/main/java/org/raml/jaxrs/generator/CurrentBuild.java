package org.raml.jaxrs.generator;

import com.google.common.collect.ArrayListMultimap;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.GenerationConfig;
import org.raml.jaxrs.generator.builders.BuildPhase;
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
import org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.resources.ResourceClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResourceMethodExtension;
import org.raml.jaxrs.generator.extension.resources.ResponseClassExtension;
import org.raml.jaxrs.generator.extension.resources.ResponseMethodExtension;
import org.raml.jaxrs.generator.extension.types.LegacyTypeExtension;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GMethod;
import org.raml.jaxrs.generator.v10.V10GResource;
import org.raml.jaxrs.generator.v10.V10GResponse;
import org.raml.v2.api.model.v10.api.Api;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

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
    private Set<JavaPoetTypeGenerator> implementations = new HashSet<>();

    private ArrayListMultimap<JavaPoetTypeGenerator, JavaPoetTypeGenerator> internalTypesPerClass = ArrayListMultimap.create();

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

                buildTypeTree(rootDirectory, (JavaPoetTypeGenerator) typeGenerator);
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

    private void buildTypeTree(final File rootDirectory, final JavaPoetTypeGenerator typeGenerator) throws IOException {
        JavaPoetTypeGenerator b = typeGenerator;

        b.output(new CodeContainer<TypeSpec.Builder>() {
                     @Override
                     public void into(final TypeSpec.Builder containing) throws IOException {

                         for (final JavaPoetTypeGenerator generator : internalTypesPerClass.get(typeGenerator)) {

                            generator.output(new CodeContainer<TypeSpec.Builder>() {

                                @Override
                                public void into(TypeSpec.Builder g) throws IOException {
                                    g.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                                    containing.addType(g.build());
                                }
                            }, BuildPhase.INTERFACE);
                         }

                         JavaFile.Builder file = JavaFile.builder(getModelPackage(), containing.build());
                         file.build().writeTo(rootDirectory);
                     }
                 },
                BuildPhase.INTERFACE
        );

        if ( implementations.contains(b) ) {

            b.output(new CodeContainer<TypeSpec.Builder>() {
                                      @Override
                                      public void into(final TypeSpec.Builder containing) throws IOException {

                                          for (final JavaPoetTypeGenerator generator : internalTypesPerClass.get(typeGenerator)) {

                                              generator.output(new CodeContainer<TypeSpec.Builder>() {

                                                  @Override
                                                  public void into(TypeSpec.Builder g) throws IOException {
                                                      g.addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                                                      containing.addType(g.build());
                                                  }
                                              }, BuildPhase.IMPLEMENTATION);
                                          }

                                          JavaFile.Builder file = JavaFile.builder(getModelPackage(), containing.build());
                                          file.build().writeTo(rootDirectory);
                                      }
                                  },
                    BuildPhase.IMPLEMENTATION);
        }
    }


    public LegacyTypeExtension withTypeListeners() {

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

    public void newImplementation(JavaPoetTypeGenerator objectType) {

        implementations.add(objectType);
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

    private <T> T buildGlobalForCreate(T defaultValue) {

        if ( configuration.getDefaultCreationExtension() != null ) {

            try {
                return (T) configuration.getDefaultCreationExtension().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new GenerationException(e);
            }
        } else {
            return defaultValue;
        }
    }

    private GlobalResourceExtension buildGlobalForFinish() {

        if ( configuration.getDefaultCreationExtension() != null ) {

            try {
                return configuration.getDefaultFinishExtension().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new GenerationException(e);
            }
        } else {
            return GlobalResourceExtension.NULL_EXTENSION;
        }
    }

    public ResourceMethodExtension<GMethod> getResourceMethodExtension(
            Annotations<ResourceMethodExtension<GMethod>> onResourceMethodExtension, GMethod gMethod) {

        if ( gMethod instanceof V10GMethod) {
            return onResourceMethodExtension.get(getApi(), ((V10GMethod) gMethod).implementation());
        }

        return onResourceMethodExtension == Annotations.ON_METHOD_CREATION ? buildGlobalForCreate(GlobalResourceExtension.NULL_EXTENSION): buildGlobalForFinish();
    }

    public ResourceClassExtension<GResource> getResourceClassExtension(ResourceClassExtension<GResource> defaultClass,
            Annotations<ResourceClassExtension<GResource>> onResourceClassCreation, GResource topResource) {
        if ( topResource instanceof V10GResource) {
            return onResourceClassCreation.get(defaultClass, getApi(), ((V10GResource) topResource).implementation());
        }

        return onResourceClassCreation == Annotations.ON_RESOURCE_CLASS_CREATION ? buildGlobalForCreate(defaultClass): buildGlobalForFinish();
    }

    public ResponseClassExtension<GMethod> getResponseClassExtension(
            Annotations<ResponseClassExtension<GMethod>> onResponseClassCreation, GMethod gMethod) {
        if ( gMethod instanceof V10GMethod ) {
            return onResponseClassCreation.get(getApi(), ((V10GMethod) gMethod).implementation());
        }

        return onResponseClassCreation == Annotations.ON_RESPONSE_CLASS_CREATION ? buildGlobalForCreate(GlobalResourceExtension.NULL_EXTENSION): buildGlobalForFinish();
    }

    public ResponseMethodExtension<GResponse> getResponseMethodExtension(
            Annotations<ResponseMethodExtension<GResponse>> onResponseMethodExtension, GResponse gResponse) {
        if ( gResponse instanceof V10GResponse) {
            return onResponseMethodExtension.get(getApi(), ((V10GResponse) gResponse).implementation());
        }

        return onResponseMethodExtension == Annotations.ON_RESPONSE_METHOD_CREATION ? buildGlobalForCreate(GlobalResourceExtension.NULL_EXTENSION): buildGlobalForFinish();
    }


    public void internalClass(JavaPoetTypeGenerator simpleTypeGenerator, JavaPoetTypeGenerator internalGenerator) {

        internalTypesPerClass.put(simpleTypeGenerator, internalGenerator);
    }
}

