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
package org.raml.jaxrs.generator;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import javax.lang.model.SourceVersion;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.math.NumberUtils.isDigits;

/**
 * <p>
 * Names class.
 * </p>
 *
 * @author kor
 * @version $Id: $Id
 *
 */
public class Names {

  private static final String PATH_REPLACEMENT_TEMPLATE = "\\{[^}]+}";
  private static Pattern LEADING_UNDERSCORES = Pattern.compile("^_+");

  public static String typeName(String... name) {
    if (name.length == 1 && isBlank(name[0])) {

      return "Root";
    }

    List<String> values = new ArrayList<>();
    int i = 0;
    for (String s : name) {
      String value = buildPart(i, s, NameFixer.CAMEL_UPPER);
      values.add(value);
      i++;
    }
    return Strings.join(values, "");
  }

  public static String methodName(String... name) {

    return checkMethodName(smallCamel(name));
  }

  private static String checkMethodName(String s) {

    if ("getClass".equals(s)) {
      return "getClazz";
    }

    if ("setClass".equals(s)) {
      return "setClazz";
    }

    return s;
  }

  private static String smallCamel(String... name) {

    if (name.length == 1 && isBlank(name[0])) {

      return "root";
    }

    List<String> values = new ArrayList<>();
    for (int i = 0; i < name.length; i++) {
      String s = name[i];
      NameFixer format = NameFixer.CAMEL_LOWER;
      values.add(buildPart(i, s, format));
    }

    return Strings.join(values, "");
  }

  public static String variableName(String... name) {

    Matcher m = LEADING_UNDERSCORES.matcher(name[0]);
    if (m.find()) {

      return m.group() + smallCamel(name);
    } else {

      return checkForReservedWord(smallCamel(name));
    }
  }

  private static String checkForReservedWord(String name) {

    if (SourceVersion.isKeyword(name)) {
      return name + "Variable";
    } else {

      return name;
    }
  }


  public static String resourceMethodName(GResource resource, GMethod method) {

    List<GParameter> parameters = ResourceUtils.accumulateUriParameters(resource);

    if (parameters.size() == 0) {

      return Names.smallCamel(method.method(),
                              resource.resourcePath().replaceAll(PATH_REPLACEMENT_TEMPLATE, ""));
    } else {

      List<String> elements = new ArrayList<>();
      elements.add(method.method());
      elements.add(resource.resourcePath().replaceAll(PATH_REPLACEMENT_TEMPLATE, ""));
      elements.add("By");
      List<String> uriparam =
          Lists.transform(parameters, new Function<GParameter, String>() {

            @Nullable
            @Override
            public String apply(@Nullable GParameter input) {
              return input.name();
            }
          });

      for (int i = 0; i < uriparam.size(); i++) {
        elements.add(uriparam.get(i));
        if (i < uriparam.size() - 1) {

          elements.add("and");
        }
      }

      return Names.smallCamel(elements.toArray(new String[elements.size()]));
    }
  }

  public static String responseClassName(GResource resource, GMethod method) {

    if (resource.uriParameters().size() == 0) {

      return Names.typeName(method.method(),
                            resource.resourcePath().replaceAll(PATH_REPLACEMENT_TEMPLATE, ""), "Response");
    } else {

      List<String> elements = new ArrayList<>();
      elements.add(method.method());
      elements.add(resource.resourcePath().replaceAll("\\{[^}]+\\}", ""));
      elements.add("By");
      List<String> uriparam =
          Lists.transform(resource.uriParameters(), new Function<GParameter, String>() {

            @Nullable
            @Override
            public String apply(@Nullable GParameter input) {
              return input.name();
            }
          });

      for (int i = 0; i < uriparam.size(); i++) {
        elements.add(uriparam.get(i));
        if (i < uriparam.size() - 1) {

          elements.add("and");
        }
      }
      elements.add("Response");

      return Names.typeName(elements.toArray(new String[elements.size()]));
    }
  }


  public static String javaTypeName(Resource resource, TypeDeclaration declaration) {
    return typeName(resource.resourcePath(), declaration.name());
  }

  public static String javaTypeName(Resource resource, Method method, TypeDeclaration declaration) {
    return typeName(resource.resourcePath(), method.method(), declaration.name());
  }

  public static String ramlTypeName(Resource resource, Method method, TypeDeclaration declaration) {
    return resource.resourcePath() + method.method() + declaration.name();
  }

  public static String ramlTypeName(Resource resource, TypeDeclaration declaration) {
    return resource.resourcePath() + declaration.name();
  }

  public static String javaTypeName(Resource resource, Method method, Response response,
                                    TypeDeclaration declaration) {
    return typeName(resource.resourcePath(), method.method(), response.code().value(),
                    declaration.name());
  }

  public static String ramlTypeName(Resource resource, Method method, Response response,
                                    TypeDeclaration declaration) {
    return resource.resourcePath() + method.method() + response.code().value() + declaration.name();
  }


  private Names() {
    throw new UnsupportedOperationException();
  }


  /**
   * <p>
   * buildJavaFriendlyName.
   * </p>
   *
   * @param source a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  private static String buildJavaFriendlyName(final String source, NameFixer format,
                                              int currentIndex) {
    final String baseName =
        source.replaceAll("\\W+", "_").replaceAll("^_+", "").replaceAll("[^\\w_]", "");
    List<String> friendlyNameBits = new ArrayList<>();
    int i = currentIndex;
    for (String s : baseName.split("_")) {

      if (s.isEmpty()) {
        continue;
      }

      String friendlyName = firstOrOthers(format, i, s);

      if (isDigits(left(friendlyName, 1))) {

        friendlyName = "_" + friendlyName;
      }

      friendlyNameBits.add(friendlyName);
      i++;
    }

    return Strings.join(friendlyNameBits, "");
  }

  private static String buildPart(int i, String s, NameFixer format) {
    String part;
    if (s.matches(".*[^a-zA-Z0-9].*")) {

      part = buildJavaFriendlyName(s, format, i);
    } else {
      part = firstOrOthers(format, i, s);
    }
    return part;
  }

  private static String firstOrOthers(NameFixer format, int i, String s) {
    if (i == 0) {
      return format.fixFirst(s);
    } else {

      return format.fixOthers(s);
    }
  }


  public static String ramlTypeName(org.raml.v2.api.model.v08.resources.Resource resource,
                                    org.raml.v2.api.model.v08.methods.Method method, BodyLike typeDeclaration) {

    return resource.resourcePath() + method.method() + typeDeclaration.name();
  }

  public static String ramlTypeName(org.raml.v2.api.model.v08.resources.Resource resource,
                                    org.raml.v2.api.model.v08.methods.Method method,
                                    org.raml.v2.api.model.v08.bodies.Response response, BodyLike typeDeclaration) {

    return resource.resourcePath() + method.method() + response.code().value()
        + typeDeclaration.name();
  }

  public static String javaTypeName(org.raml.v2.api.model.v08.resources.Resource resource,
                                    org.raml.v2.api.model.v08.methods.Method method, BodyLike typeDeclaration) {
    return typeName(resource.resourcePath(), method.method(), typeDeclaration.name());
  }

  public static String javaTypeName(org.raml.v2.api.model.v08.resources.Resource resource,
                                    org.raml.v2.api.model.v08.methods.Method method,
                                    org.raml.v2.api.model.v08.bodies.Response response, BodyLike typeDeclaration) {
    return typeName(resource.resourcePath(), method.method(), response.code().value(),
                    typeDeclaration.name());
  }

}
