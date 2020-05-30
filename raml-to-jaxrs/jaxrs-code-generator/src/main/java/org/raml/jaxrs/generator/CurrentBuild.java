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
import com.google.common.base.Suppliers;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.io.Files;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsonschema2pojo.GenerationConfig;
import org.raml.jaxrs.generator.builders.*;
import org.raml.jaxrs.generator.builders.resources.ResourceGenerator;
import org.raml.jaxrs.generator.extension.resources.api.*;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GResponse;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.ExtensionManager;
import org.raml.jaxrs.generator.v10.JaxRsAnnotations;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.types.V10RamlToPojoGType;
import org.raml.ramltopojo.*;
import org.raml.ramltopojo.plugin.PluginManager;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

/**
 * Created by Jean-Philippe Belanger on 10/26/16. The art of building stuff is here. Factory for building root stuff.
 */
public class CurrentBuild {

  private final Document api;
  private final ExtensionManager extensionManager;

  private final List<ResourceGenerator> resources = new ArrayList<>();
  private final Map<String, TypeGenerator<?>> builtTypes = new ConcurrentHashMap<>();
  private final PluginManager pluginManager = PluginManager.createPluginManager("META-INF/ramltojaxrs-plugin.properties");

  private final GlobalResourceExtension.Composite resourceExtensionList = new GlobalResourceExtension.Composite();

  private final Map<String, Pair<GType, GeneratorType>> foundTypes = new HashMap<>();

  private final List<JavaPoetTypeGenerator> supportGenerators = new ArrayList<>();
  private Configuration configuration;
  private final ArrayListMultimap<JavaPoetTypeGenerator, JavaPoetTypeGenerator> internalTypesPerClass = ArrayListMultimap
      .create();
  private File schemaRepository;

  private final Supplier<RamlToPojo> ramlToPojo;

  public CurrentBuild(Document api, ExtensionManager extensionManager,
                      Function<CurrentBuild, ExtendedFoundCallback> foundCallbackFactory) {

    this.api = api;
    this.extensionManager = extensionManager;
    this.configuration = Configuration.defaultConfiguration();
    this.ramlToPojo =
        Suppliers.memoize(() ->
            RamlToPojoBuilder
                .builder(this.api)
                .inPackage(getModelPackage())
                .typeFinder(foundCallbackFactory.apply(this))
                .build(this.typeConfiguration().stream().map(s -> s.contains(".") ? s : "core." + s)
                    .collect(Collectors.toList())));
  }

  public RamlToPojo fetchRamlToPojoBuilder() {

    return ramlToPojo.get();
  }

  public File getSchemaRepository() {

    if (schemaRepository == null) {

      schemaRepository = Files.createTempDir();
    }

    return schemaRepository;
  }

  public Document getApi() {
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

      for (TypeGenerator<?> typeGenerator : builtTypes.values()) {

        if (typeGenerator instanceof RamlToPojoTypeGenerator) {

          RamlToPojoTypeGenerator ramlToPojoTypeGenerator = (RamlToPojoTypeGenerator) typeGenerator;
          ramlToPojoTypeGenerator.output(g -> g.createFoundTypes(rootDirectory.getAbsolutePath()));
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

          b.output(g -> g.build(rootDirectory));
        }
      }

      // todo fix this: not loops, lambdas.
      for (ResourceGenerator resource : resources) {
        resource.output(g -> JavaFile.builder(getResourcePackage(), g).skipJavaLangImports(true).build().writeTo(rootDirectory));
      }

      for (JavaPoetTypeGenerator typeGenerator : supportGenerators) {

        typeGenerator.output(g -> JavaFile.builder(getSupportPackage(), g.build()).skipJavaLangImports(true).build()
            .writeTo(rootDirectory));
      }

    } finally {

      if (schemaRepository != null) {

        FileUtils.deleteDirectory(schemaRepository);
      }
    }
  }

  private void buildTypeTree(final File rootDirectory, final JavaPoetTypeGenerator typeGenerator) throws IOException {
    JavaPoetTypeGenerator b = typeGenerator;

    b.output(containing -> {

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
    },
             BuildPhase.INTERFACE
        );
  }

  public void newGenerator(String ramlTypeName, TypeGenerator<?> generator) {

    builtTypes.put(ramlTypeName, generator);
  }

  public void newSupportGenerator(JavaPoetTypeGenerator generator) {

    supportGenerators.add(generator);
  }

  public void newResource(ResourceGenerator rg) {

    resources.add(rg);
  }

  public void constructObjectClasses(GFinder finder) {

    try {
      finder.setupConstruction(this);
      fetchRamlToPojoBuilder().buildPojos().createFoundTypes(this.configuration.getOutputDirectory().toString());
      for (GeneratorType type : foundTypes.values().stream().map(Pair::getRight).collect(Collectors.toList())) {

        type.construct(this);
      }
    } catch (IOException e) {
      throw new GenerationException(e);
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

    List<String> configuredPlugins = this.typeConfiguration().stream()
        .map((Function<String, String>) s -> "ramltojaxrs." + s)
        .collect(Collectors.toList());

    for (String basePlugin : configuredPlugins) {
      plugins.addAll(pluginManager.getClassesForName(basePlugin, Collections.<String>emptyList(), pluginType));
    }
  }

  public ResourceClassExtension pluginsForResourceClass(EndPoint endpoint) {

    List<PluginDef> data = JaxRsAnnotations.RESOURCE_PLUGINS.get(Collections.emptyList(), endpoint);
    Set<ResourceClassExtension> plugins = buildPluginList(ResourceClassExtension.class, data);

    return new ResourceClassExtension.Composite(plugins);
  }

  public ResponseClassExtension pluginsForResponseClass(Function<Collection<ResponseClassExtension>, ResponseClassExtension> provider,
                                                        GMethod method) {

    List<PluginDef> data = Collections.emptyList(); // todo
                                                    // Annotations.RESPONSE_CLASS_PLUGINS.get(Collections.<PluginDef>emptyList(),
                                                    // api, method);
    Set<ResponseClassExtension> plugins = buildPluginList(ResponseClassExtension.class, data);

    // Ugly, but I don't care
    return provider.apply(plugins.stream().map(x -> x).collect(Collectors.toList()));
  }

  public ResourceMethodExtension pluginsForResourceMethod(Function<Collection<ResourceMethodExtension>, ResourceMethodExtension> provider,
                                                          GMethod resource) {

    List<PluginDef> data = Collections.emptyList(); // todo Annotations.METHOD_PLUGINS.get(Collections.<PluginDef>emptyList(),
                                                    // api, resource);
    Set<ResourceMethodExtension> plugins = buildPluginList(ResourceMethodExtension.class, data);

    // Ugly, but I don't care
    return provider.apply(plugins.stream().map(x -> x).collect(Collectors.toList()));
  }

  public ResponseMethodExtension pluginsForResponseMethod(Function<Collection<ResponseMethodExtension>, ResponseMethodExtension> provider,
                                                          GResponse resource) {

    List<PluginDef> data = Collections.emptyList(); // todo Annotations.RESPONSE_PLUGINS.get(Collections.<PluginDef>emptyList(),
                                                    // api, resource);
    Set<ResponseMethodExtension> plugins = buildPluginList(ResponseMethodExtension.class, data);

    // Ugly, but I don't care
    return provider.apply(plugins.stream().map(x -> x).collect(Collectors.toList()));
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
      Class<?> c = Class.forName(className);
      return (Iterable<T>) singletonList(c.newInstance());
    } catch (ClassNotFoundException e) {

      return extensionManager.getClassesForName(className).stream().map(input -> {
        try {
          return (T) input.newInstance();
        } catch (InstantiationException | IllegalAccessException e1) {

          throw new GenerationException(e1);
        }
      }).collect(Collectors.toList());
    } catch (IllegalAccessException | InstantiationException e) {
      throw new GenerationException(e);
    }
  }

  public GlobalResourceExtension withResourceListeners() {

    return resourceExtensionList;
  }

  // todo: wrong and a bit useless. Fix me
  public V10GType fetchType(AnyShape anyShape) {
    TypeName typeName =
        fetchRamlToPojoBuilder()
            .fetchTypeName(anyShape).orElseThrow(() -> new GenerationException("can't find type for " + anyShape.name() + "("
                + anyShape.id() + ")"));

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

  public Optional<TypeName> fetchTypeName(AnyShape anyShape) {
    return fetchRamlToPojoBuilder().fetchTypeName(anyShape);
  }
}
