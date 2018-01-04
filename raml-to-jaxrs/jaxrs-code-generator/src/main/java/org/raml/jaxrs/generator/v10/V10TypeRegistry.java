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
package org.raml.jaxrs.generator.v10;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.jaxrs.generator.v10.types.V10GTypeFactory;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/30/16. Just potential zeroes and ones This one just make sure that the Type Classes are
 * created only once...
 */

public class V10TypeRegistry {

  private final CurrentBuild build;
  private Map<String, V10GType> types = new HashMap<>();

  public V10TypeRegistry(CurrentBuild build) {
    this.build = build;
  }



  private void storeNewType(V10GType type) {
    if (!type.isScalar()) {
      types.put(type.name(), type);
    }
  }


  public V10GType fetchType(String name, TypeDeclaration typeDeclaration) {

    Class<?> javaType = ScalarTypes.scalarToJavaType(typeDeclaration);
    if (javaType != null) {

      if (typeDeclaration instanceof StringTypeDeclaration
          && ((StringTypeDeclaration) typeDeclaration).enumValues().size() > 0) {

        if (types.containsKey(name)) {
          return types.get(name);
        } else {
          V10GType type =
              V10GTypeFactory.createEnum(name, (StringTypeDeclaration) typeDeclaration);
          storeNewType(type);
          return type;
        }
      } else {
        return V10GTypeFactory.createScalar(name, typeDeclaration);
      }
    }

    if (typeDeclaration instanceof ArrayTypeDeclaration) {

      return V10GTypeFactory.createArray(this, name, (ArrayTypeDeclaration) typeDeclaration, CreationModel.NEVER_INLINE);
    }

    if (types.containsKey(name)) {

      return types.get(name);
    } else {

      V10GType type;
      if (typeDeclaration instanceof JSONTypeDeclaration) {
        type =
            V10GTypeFactory.createJson((JSONTypeDeclaration) typeDeclaration,
                                       typeDeclaration.name(), CreationModel.INLINE_FROM_TYPE);
      } else if (typeDeclaration instanceof XMLTypeDeclaration) {
        type =
            V10GTypeFactory.createXml((XMLTypeDeclaration) typeDeclaration, typeDeclaration.name(),
                                      CreationModel.INLINE_FROM_TYPE);
      } else if (typeDeclaration instanceof UnionTypeDeclaration) {
        type =
            V10GTypeFactory.createUnion(typeDeclaration.name(), (UnionTypeDeclaration) typeDeclaration
                );
      } else {
        type = V10GTypeFactory.createExplicitlyNamedType(name, typeDeclaration);
      }
      storeNewType(type);
      return type;
    }
  }

  public List<V10GType> fetchSchemaTypes() {

    return FluentIterable.from(types.values()).filter(new Predicate<V10GType>() {

      @Override
      public boolean apply(@Nullable V10GType input) {
        return input.isJson() || input.isXml();
      }
    }).toList();
  }
}
