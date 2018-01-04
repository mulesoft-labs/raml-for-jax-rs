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
package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.CreationModel;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/4/17. Just potential zeroes and ones
 */
public class V10GTypeScalar extends V10GTypeHelper {

  private final TypeDeclaration scalar;
  private final String ramlName;


  public V10GTypeScalar(String name, TypeDeclaration scalar) {
    super(name, scalar, CreationModel.NEVER_INLINE);
    this.scalar = scalar;
    this.ramlName = name;
  }

  @Override
  public TypeDeclaration implementation() {
    return scalar;
  }

  @Override
  public String type() {
    return ramlName;
  }

  @Override
  public String name() {
    return ramlName;
  }

  @Override
  public boolean isScalar() {
    return true;
  }

  @Override
  public TypeName defaultJavaTypeName(String pack) {

    String annotation = Annotations.CLASS_NAME.get(null, scalar);
    if (annotation == null) {

      return ScalarTypes.classToTypeName(ScalarTypes.scalarToJavaType(scalar));
    } else {

      if (annotation.contains(".")) {
        return ClassName.bestGuess(annotation);
      } else {
        return ClassName.get(pack, annotation);
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (!(o instanceof V10GType)) {

      return false;
    }

    V10GType v10GType = (V10GType) o;

    return ramlName.equals(v10GType.name());
  }

  @Override
  public int hashCode() {
    return ramlName.hashCode();
  }

}
