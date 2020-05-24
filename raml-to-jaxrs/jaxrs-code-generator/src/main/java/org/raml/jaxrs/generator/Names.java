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

import amf.client.model.domain.*;

import javax.lang.model.SourceVersion;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    return String.join("", values);
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

    return String.join("", values);
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


  public static String resourceMethodName(EndPoint resource, Operation method) {

    List<Parameter> parameters = ResourceUtils.accumulateUriParameters(resource);

    if (parameters.size() == 0) {

      return Names.smallCamel(method.method().value(),
                              resource.path().value().replaceAll(PATH_REPLACEMENT_TEMPLATE, ""));
    } else {

      List<String> elements = new ArrayList<>();
      elements.add(method.method().value());
      elements.add(resource.path().value().replaceAll(PATH_REPLACEMENT_TEMPLATE, ""));
      elements.add("By");
      List<String> uriparam =
          parameters.stream().map(x -> x.name().value()).collect(Collectors.toList());

      for (int i = 0; i < uriparam.size(); i++) {
        elements.add(uriparam.get(i));
        if (i < uriparam.size() - 1) {

          elements.add("and");
        }
      }

      return Names.smallCamel(elements.toArray(new String[0]));
    }
  }

  public static String responseClassName(EndPoint resource, Operation method) {

    List<Parameter> parameters = ResourceUtils.accumulateUriParameters(resource);

    if (parameters.size() == 0) {

      return Names.typeName(method.method().value(),
                            resource.path().value().replaceAll(PATH_REPLACEMENT_TEMPLATE, ""), "Response");
    } else {

      List<String> elements = new ArrayList<>();
      elements.add(method.method().value());
      elements.add(resource.path().value().replaceAll("\\{[^}]+\\}", ""));
      elements.add("By");
      List<String> uriparam =
          parameters.stream().map(input -> input.name().value()).collect(Collectors.toList());

      for (int i = 0; i < uriparam.size(); i++) {
        elements.add(uriparam.get(i));
        if (i < uriparam.size() - 1) {

          elements.add("and");
        }
      }
      elements.add("Response");

      return Names.typeName(elements.toArray(new String[0]));
    }
  }


  public static String javaTypeName(EndPoint resource, Operation method, AnyShape anyShape) {
    return typeName(resource.path().value(), method.method().value(), anyShape.name().value());
  }


  public static String ramlRawTypeName(String... names) {
    return Arrays.stream(names).map(n -> n.replace(File.separatorChar + "", "_")).collect(Collectors.joining("_"));
  }

  public static String javaRawTypeName(String... names) {
    return typeName(names);
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

      if (i == 0 && isDigits(left(friendlyName, 1))) {

        friendlyName = "_" + friendlyName;
      }

      friendlyNameBits.add(friendlyName);
      i++;
    }

    return String.join("", friendlyNameBits);
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


  public static String ramlTypeName(EndPoint resource,
                                    Operation method, Payload typeDeclaration) {

    return resource.path().value() + method.method() + typeDeclaration.name().value();
  }

  public static String ramlTypeName(EndPoint resource,
                                    Operation method,
                                    Response response, Payload typeDeclaration) {

    return resource.path().value() + method.method() + response.statusCode().value()
        + typeDeclaration.name().value();
  }

  public static String javaTypeName(EndPoint resource,
                                    Operation method, Payload typeDeclaration) {
    return typeName(resource.path().value(), method.method().value(), typeDeclaration.name().value());
  }

  public static String javaTypeName(EndPoint resource,
                                    Operation method,
                                    Response response, Payload typeDeclaration) {
    return typeName(resource.path().value(), method.method().value(), response.statusCode().value(),
                    typeDeclaration.name().value());
  }

}
