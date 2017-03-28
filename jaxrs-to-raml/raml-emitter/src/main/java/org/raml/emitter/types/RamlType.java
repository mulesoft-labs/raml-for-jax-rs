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
package org.raml.emitter.types;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import org.raml.api.RamlEntity;
import org.raml.api.ScalarType;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 3/26/17. Just potential zeroes and ones
 */

public class RamlType {

  private final RamlEntity type;

  private final boolean collection;

  private Map<String, RamlProperty> properties = new HashMap<>();
  private List<RamlType> superTypes;

  public RamlType(RamlEntity type) {

    this.type = type;
    this.collection = false;
  }

  public RamlType(RamlEntity type, boolean collection) {

    this.type = type;
    this.collection = collection;
  }

  public static RamlType collectionOf(RamlType collectionType) {

    return new RamlType(collectionType.type, true);
  }

  public void addProperty(RamlProperty property) {

    properties.put(property.getName(), property);
  }

  public void write(IndentedAppendable writer) throws IOException {

    Class c = (Class) type.getType();
    writer.appendLine(c.getSimpleName() + ":");
    writer.indent();

    if (superTypes != null && superTypes.size() > 0) {
      writer.appendLine("type: [ " + Joiner.on(", ").join(Collections2.transform(superTypes, new Function<RamlType, String>() {

        @Override
        public String apply(RamlType input) {
          return input.getTypeName();
        }
      })) + " ]");
    }

    if (type.getDescription().isPresent()) {
      writer.appendLine("description: " + type.getDescription().get());
    }

    for (RamlProperty ramlProperty : properties.values()) {

      ramlProperty.write(writer);
    }

    writer.outdent();
  }

  public String getTypeName() {

    Optional<ScalarType> st = ScalarType.fromType(type.getType());
    if (st.isPresent()) {
      if (collection == true) {
        return st.get().getRamlSyntax() + "[]";
      } else {
        return st.get().getRamlSyntax();
      }
    } else {

      Class c = (Class) type.getType();
      if (collection == true) {
        return c.getSimpleName() + "[]";
      } else {
        return c.getSimpleName();
      }
    }
  }


  public void setSuperTypes(List<RamlType> superTypes) {
    this.superTypes = superTypes;
  }


}
