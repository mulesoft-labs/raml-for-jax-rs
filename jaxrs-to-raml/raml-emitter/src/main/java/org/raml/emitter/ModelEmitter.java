/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
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
package org.raml.emitter;

import com.google.common.base.Optional;
import org.raml.api.RamlApi;
import org.raml.api.RamlSupportedAnnotation;
import org.raml.api.ScalarType;
import org.raml.builder.AnnotationTypeBuilder;
import org.raml.builder.RamlDocumentBuilder;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.GrammarPhase;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import static org.raml.builder.NodeBuilders.key;
import static org.raml.builder.NodeBuilders.property;
import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

/**
 * Created. There, you have it.
 */
public class ModelEmitter implements Emitter {

  private PrintWriter writer;

  public ModelEmitter(PrintWriter writer) {

    this.writer = writer;
  }

  @Override
  public void emit(RamlApi modelApi) throws RamlEmissionException {

    org.raml.simpleemitter.Emitter emitter = new org.raml.simpleemitter.Emitter();

    RamlDocumentBuilder builder = RamlDocumentBuilder.document();
    try {
      builder
          .with(

                property("title", modelApi.getTitle()),
                property("baseUri", modelApi.getBaseUri()),
                property("version", modelApi.getVersion()),
                property("mediaType", modelApi.getDefaultMediaType().toStringRepresentation())
          );
      annotationTypes(builder, modelApi);
    } catch (IOException e) {

      throw new RamlEmissionException("trying to emit", e);
    }

    Api api = builder.build();
    final GrammarPhase grammarPhase =
        new GrammarPhase(RamlHeader.getFragmentRule(new RamlHeader(RAML_10, Default).getFragment()));
    Node node = ((NodeModel) api).getNode();
    grammarPhase.apply(node);

    List<ErrorNode> errors = node.findDescendantsWith(ErrorNode.class);
    for (ErrorNode error : errors) {
      System.err.println("error: " + error.getErrorMessage());
    }
    if (errors.size() == 0) {
      try {
        emitter.emit(api, writer);
      } catch (IOException e) {
        throw new RamlEmissionException("trying to emit", e);
      }
    }

  }

  private void annotationTypes(RamlDocumentBuilder builder, RamlApi modelApi) throws IOException {

    for (RamlSupportedAnnotation ramlSupportedAnnotation : modelApi.getSupportedAnnotation()) {

      AnnotationTypeBuilder annotationTypeBuilder =
          AnnotationTypeBuilder.annotationType(ramlSupportedAnnotation.getAnnotation().getSimpleName());

      Class<? extends Annotation> javaAnnotation = ramlSupportedAnnotation.getAnnotation();
      if (javaAnnotation.getDeclaredMethods().length > 0) {
        for (Method method : javaAnnotation.getDeclaredMethods()) {

          if (method.getReturnType().isArray()) {
            annotationTypeBuilder.withProperty(property(method.getName(), calculateRamlType(method.getReturnType()
                .getComponentType()) + "[]"));
          } else {
            annotationTypeBuilder.withProperty(property(method.getName(), calculateRamlType(method.getReturnType())
                ));
          }
        }

        builder.withAnnotationTypes(annotationTypeBuilder);
      }
    }
  }

  public String calculateRamlType(Class<?> type) throws IOException {

    if (Class.class.equals(type)) {

      return "string";
    }
    Optional<ScalarType> scalarType = ScalarType.fromType(type);
    if (scalarType.isPresent()) {

      return scalarType.get().getRamlSyntax();
    }

    throw new IOException("invalid type for annotation: " + type);
  }

  /*
   * writer.appendLine("annotationTypes:"); writer.indent(); for (RamlSupportedAnnotation ramlSupportedAnnotation :
   * suportedAnnotations) { Class<? extends Annotation> javaAnnotation = ramlSupportedAnnotation.getAnnotation();
   * 
   * if (javaAnnotation.getDeclaredMethods().length > 0) { writer.appendLine(javaAnnotation.getSimpleName() + ":");
   * writer.indent(); writer.appendLine("properties:"); writer.indent(); for (Method method : javaAnnotation.getDeclaredMethods())
   * {
   * 
   * if (method.getReturnType().isArray()) { writer.appendLine(method.getName() + ": " +
   * calculateRamlType(method.getReturnType().getComponentType()) + "[]"); } else { writer.appendLine(method.getName() + ": " +
   * calculateRamlType(method.getReturnType())); } } writer.outdent(); writer.outdent(); } else {
   * 
   * writer.appendLine(javaAnnotation.getSimpleName() + ": nil"); } }
   * 
   * writer.outdent();
   */
}
