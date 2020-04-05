/*
 * Copyright 2013-2018 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.generator;

import amf.client.model.document.Document;
import amf.client.model.domain.*;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.codemodel.JCodeModel;
import japa.parser.ast.body.TypeDeclaration;
import org.apache.commons.io.FileUtils;
import org.jsonschema2pojo.GenerationConfig;
import org.raml.jaxrs.generator.builders.*;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;
import org.raml.jaxrs.generator.extension.resources.api.*;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.v10.*;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.types.V10RamlToPojoGType;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.plugin.PluginManager;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.singletonList;

/**
 * Created by Jean-Philippe Belanger on 10/26/16. The art of building stuff is here. Factory for building root stuff.
 */
public class CurrentBuild {

  private final Document api;
  private ExtensionManager extensionManager;
  private final File ramlDirectory;

  private final List<ResourceGenerator> resources = new ArrayList<>();
  private final Map<String, TypeGenerator> builtTypes = new ConcurrentHashMap<>();
  private final PluginManager pluginManager = PluginManager.createPluginManager("META-INF/ramltojaxrs-plugin.properties");

  private GlobalResourceExtension.Composite resourceExtensionList = new GlobalResourceExtension.Composite();

  private Map<String, GeneratorType> foundTypes = new HashMap<>();

  private final List<JavaPoetTypeGenerator> supportGenerators = new ArrayList<>();
  private Configuration configuration;
  private Set<JavaPoetTypeGenerator> implementations = new HashSet<>();

  private ArrayListMultimap<JavaPoetTypeGenerator, JavaPoetTypeGenerator> internalTypesPerClass = ArrayListMultimap.create();

  private File schemaRepository;

  private RamlToPojo ramlToPojo;

  public CurrentBuild(Document api, ExtensionManager extensionManager) {

    this.api = api;
    this.extensionManager = extensionManager;
    this.ramlDirectory = ramlDirectory;
    this.configuration = Configuration.defaultConfiguration();
  }


  public File getRamlDirectory() {
    return ramlDirectory;
  }

  public RamlToPojo fetchRamlToPojoBuilder() {

    if (ramlToPojo == null) {

      ramlToPojo = RamlToPojoBuilder.builder(this.api)
          .inPackage(getModelPackage())
          .fetchTypes(TypeFetchers.fromAnywhere())
          .build(FluentIterable.from(this.typeConfiguration()).transform(new Function<String, String>() {

            @Nullable
            @Override
            public String apply(@Nullable String s) {
              if (s.contains(".")) {
                return s;
              } else {
                return "core." + s;
              }
            }
          }).toList());
    }

    return ramlToPojo;
  }


  public File getSchemaRepository() {

    if (schemaRepository == null) {

      schemaRepository = Files.createTempDir();
    }

    return schemaRepository;
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

    try {
      if (resources.size() > 0 && shouldGenerateResponseClasses()) {
        ResponseSupport.buildSupportClasses(rootDirectory, getSupportPackage());
      }

      for (TypeGenerator typeGenerator : builtTypes.values()) {

        if (typeGenerator instanceof RamlToPojoTypeGenerator) {

          RamlToPojoTypeGenerator ramlToPojoTypeGenerator = (RamlToPojoTypeGenerator) typeGenerator;
          ramlToPojoTypeGenerator.output(new CodeContainer<ResultingPojos>() {

            @Override
            public void into(ResultingPojos g) throws IOException {
              g.createFoundTypes(rootDirectory.getAbsolutePath());
            }
          });
        }
        if (typeGenerator instanceof JavaPoetTypeGenerator) {

          buildTypeTree(rootDirectory, (JavaPoetTypeGenerator) typeGenerator);
          continue;
        }

        if (typeGenerator instanceof CodeModelTypeGenerator) {
          CodeModelTypeGenerator b = (CodeModelTypeGenerator) typeGenerator;
          if (!rootDirectory.exists()) {
            java.nio.file.Files.createDirectories(rootDirectory.toPath());
          }

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
            JavaFile.Builder file = JavaFile.builder(getResourcePackage(), g).skipJavaLangImports(true);
            file.build().writeTo(rootDirectory);
          }
        });
      }

      for (JavaPoetTypeGenerator typeGenerator : supportGenerators) {

        typeGenerator.output(new CodeContainer<TypeSpec.Builder>() {

          @Override
          public void into(TypeSpec.Builder g) throws IOException {

            JavaFile.Builder file = JavaFile.builder(getSupportPackage(), g.build()).skipJavaLangImports(true);
            file.build().writeTo(rootDirectory);
          }
        });
      }

    } finally {

      if (schemaRepository != null) {

        FileUtils.deleteDirectory(schemaRepository);
      }
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

        if (containing != null) {

          JavaFile.Builder file = JavaFile.builder(getModelPackage(), containing.build()).skipJavaLangImports(true);
          file.build().writeTo(rootDirectory);
        }
      }
    },
             BuildPhase.INTERFACE
        );

    if (implementations.contains(b)) {

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

          if (containing != null) {
            JavaFile.Builder file = JavaFile.builder(getModelPackage(), containing.build()).skipJavaLangImports(true);
            file.build().writeTo(rootDirectory);
          }
        }
      },
               BuildPhase.IMPLEMENTATION);
    }
  }

  public void newGenerator(String ramlTypeName, TypeGenerator generator) {

    §builtTypes.put(ramlTypeName, generator);
  }

  public void newSupportGenerator(JavaPoetTypeGenerator generator) {

    supportGenerators.add(generator);
  }

  public void newResource(ResourceGenerator rg) {

    resources.add(rg);
  }

  public void constructClasses(GFinder finder) {

    TypeFindingListener listener = new TypeFindingListener(foundTypes);
    finder.findTypes(listener);

    finder.setupConstruction(this);
    for (GeneratorType type : foundTypes.values()) {

      type.construct(this);
    }
  }


  public List<String> typeConfiguration() {

    return Arrays.asList(this.configuration.getTypeConfiguration());
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  public GenerationConfig getJsonMapperConfig() {
    return configuration.createJsonSchemaGenerationConfig();
  }

  private <T> void loadBasePlugins(Set<T> plugins, Class<T> pluginType) {

    List<String> configuredPlugins = FluentIterable.from(this.typeConfiguration()).transform(new Function<String, String>() {

      @Nullable
      @Override
      public String apply(@Nullable String s) {
        return "ramltojaxrs." + s;
      }
    }).toList();

    for (String basePlugin : configuredPlugins) {
      plugins.addAll(pluginManager.getClassesForName(basePlugin, Collections.<String>emptyList(), pluginType));
    }
  }

  public ResourceClassExtension<GResource> pluginsForResourceClass(Function<Collection<ResourceClassExtension<GResource>>, ResourceClassExtension<GResource>> provider,
                                                                   GResource resource) {

    List<PluginDef> data = Annotations.RESOURCE_PLUGINS.get(Collections.<PluginDef>emptyList(), api, resource);
    Set<ResourceClassExtension> plugins = buildPluginList(ResourceClassExtension.class, data);

    // Ugly, but I don't care
    return provider.apply(FluentIterable.from(plugins)
        .transform(new Function<ResourceClassExtension, ResourceClassExtension<GResource>>() {

          @Nullable
          @Override
          public ResourceClassExtension<GResource> apply(@Nullable ResourceClassExtension resourceClassExtension) {
            return resourceClassExtension;
          }
        }).toList());
  }

  public ResponseClassExtension<GMethod> pluginsForResponseClass(Function<Collection<ResponseClassExtension<GMethod>>, ResponseClassExtension<GMethod>> provider,
                                                                 GMethod method) {

    List<PluginDef> data = Annotations.RESPONSE_CLASS_PLUGINS.get(Collections.<PluginDef>emptyList(), api, method);
    Set<ResponseClassExtension> plugins = buildPluginList(ResponseClassExtension.class, data);

    // Ugly, but I don't care
    return provider.apply(FluentIterable.from(plugins)
        .transform(new Function<ResponseClassExtension, ResponseClassExtension<GMethod>>() {

          @Nullable
          @Override
          public ResponseClassExtension<GMethod> apply(@Nullable ResponseClassExtension resourceClassExtension) {
            return resourceClassExtension;
          }
        }).toList());
  }

  public ResourceMethodExtension<GMethod> pluginsForResourceMethod(Function<Collection<ResourceMethodExtension<GMethod>>, ResourceMethodExtension<GMethod>> provider,
                                                                   GMethod resource) {

    List<PluginDef> data = Annotations.METHOD_PLUGINS.get(Collections.<PluginDef>emptyList(), api, resource);
    Set<ResourceMethodExtension> plugins = buildPluginList(ResourceMethodExtension.class, data);

    // Ugly, but I don't care
    return provider.apply(FluentIterable.from(plugins)
        .transform(new Function<ResourceMethodExtension, ResourceMethodExtension<GMethod>>() {

          @Nullable
          @Override
          public ResourceMethodExtension<GMethod> apply(@Nullable ResourceMethodExtension resourceClassExtension) {
            return resourceClassExtension;
          }
        }).toList());
  }

  public ResponseMethodExtension<GResponse> pluginsForResponseMethod(Function<Collection<ResponseMethodExtension<GResponse>>, ResponseMethodExtension<GResponse>> provider,
                                                                     GResponse resource) {

    List<PluginDef> data = Annotations.RESPONSE_PLUGINS.get(Collections.<PluginDef>emptyList(), api, resource);
    Set<ResponseMethodExtension> plugins = buildPluginList(ResponseMethodExtension.class, data);

    // Ugly, but I don't care
    return provider.apply(FluentIterable.from(plugins)
        .transform(new Function<ResponseMethodExtension, ResponseMethodExtension<GResponse>>() {

          @Nullable
          @Override
          public ResponseMethodExtension<GResponse> apply(@Nullable ResponseMethodExtension resourceClassExtension) {
            return resourceClassExtension;
          }
        }).toList());
  }

  private <T> Set<T> buildPluginList(Class<T> cls, List<PluginDef> data) {
    Set<T> plugins = new LinkedHashSet<>();
    loadBasePlugins(plugins, cls);
    for (PluginDef datum : data) {
      plugins.addAll(pluginManager.getClassesForName(datum.getPluginName(), datum.getArguments(), cls));
    }
    return plugins;
  }

  public <T> Iterable<T> createExtensions(String className) {

    try {
      Class c = Class.forName(className);
      return (Iterable<T>) singletonList(c.newInstance());
    } catch (ClassNotFoundException e) {


      return FluentIterable.from(extensionManager.getClassesForName(className)).transform(new Function<Class, T>() {

        @Nullable
        @Override
        public T apply(@Nullable Class input) {
          try {
            return (T) input.newInstance();
          } catch (InstantiationException | IllegalAccessException e1) {

            throw new GenerationException(e1);
          }
        }
      });
    } catch (IllegalAccessException | InstantiationException e) {
      throw new GenerationException(e);
    }
  }

  public GlobalResourceExtension withResourceListeners() {

    return resourceExtensionList;
  }

  public V10GType fetchType(EndPoint implementation, Operation method, AnyShape anyShape) {


    if (anyShape instanceof SchemaShape) {

      return (V10GType) ((JsonSchemaTypeGenerator) builtTypes.get(anyShape.name().value())).getType();
    }

    // todo handling XML
//    if (anyShape instanceof SchemaShape) {
//
//      return (V10GType) ((XmlSchemaTypeGenerator) builtTypes.get(anyShape.type())).getType();
//    }

    RamlToPojo ramlToPojo = fetchRamlToPojoBuilder();
    if (ramlToPojo.isInline(anyShape)) {

      TypeName typeName =
          ramlToPojo
              .fetchType(Names.javaTypeName(implementation, method, anyShape), anyShape);
      V10RamlToPojoGType type =
          new V10RamlToPojoGType(Names.javaTypeName(implementation, method, anyShape), anyShape);
      type.setJavaType(typeName);
      return type;
    } else {

      TypeName typeName =
          ramlToPojo
              .fetchType(anyShape.name().value(), anyShape);
      V10RamlToPojoGType type = new V10RamlToPojoGType(anyShape.type(), anyShape);
      type.setJavaType(typeName);
      return type;
    }

  }

  public V10GType fetchType(EndPoint resource, AnyShape input) {

    RamlToPojo ramlToPojo = fetchRamlToPojoBuilder();
    if (ramlToPojo.isInline(input)) {

      TypeName typeName =
          fetchRamlToPojoBuilder()
              .fetchType(Names.javaTypeName(resource, input), input);

      V10RamlToPojoGType type = new V10RamlToPojoGType(input);
      type.setJavaType(typeName);
      return type;
    } else {

      TypeName typeName =
          fetchRamlToPojoBuilder()
              .fetchType(input.type(), input);

      V10RamlToPojoGType type = new V10RamlToPojoGType(input);
      type.setJavaType(typeName);
      return type;
    }
  }

  public V10GType fetchType(EndPoint resource, Operation method, Response response, AnyShape anyShape) {

    if (anyShape instanceof JSONTypeDeclaration) {

      return (V10GType) ((JsonSchemaTypeGenerator) builtTypes.get(anyShape.type())).getType();
    }

    if (anyShape instanceof XMLTypeDeclaration) {

      return (V10GType) ((XmlSchemaTypeGenerator) builtTypes.get(anyShape.type())).getType();
    }


    RamlToPojo ramlToPojo = fetchRamlToPojoBuilder();
    if (ramlToPojo.isInline(anyShape)) {

      TypeName typeName =
          fetchRamlToPojoBuilder()
              .fetchType(Names.javaTypeName(resource, method, response, anyShape), anyShape);

      V10RamlToPojoGType type =
          new V10RamlToPojoGType(Names.javaTypeName(resource, method, response, anyShape), anyShape);
      type.setJavaType(typeName);
      return type;
    } else {

      TypeName typeName =
          fetchRamlToPojoBuilder()
              .fetchType(anyShape.type(), anyShape);

      V10RamlToPojoGType type = new V10RamlToPojoGType(anyShape.type(), anyShape);
      type.setJavaType(typeName);
      return type;
    }
  }

  public V10GType fetchType(String name, AnyShape anyShape) {
    TypeName typeName =
        fetchRamlToPojoBuilder()
            .fetchType(name, anyShape);

    V10RamlToPojoGType type = new V10RamlToPojoGType(anyShape);
    type.setJavaType(typeName);
    return type;
  }

  public boolean shouldCopySchemas() {
    return configuration.getCopySchemas();
  }

  public boolean shouldGenerateResponseClasses() {
    return configuration.getGenerateResponseClasses();
  }
}
