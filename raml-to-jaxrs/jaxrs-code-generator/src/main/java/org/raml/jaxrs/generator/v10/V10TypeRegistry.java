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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
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

  private Map<String, V10GType> types = new HashMap<>();
  private Multimap<String, V10GType> childMap = ArrayListMultimap.create();

  public void addChildToParent(List<V10GType> parents, V10GType child) {

    for (V10GType parent : parents) {

      if (parent.name().equals("object")) {
        continue;
      }
      childMap.put(parent.name(), child);
      addChildToParent(parent.parentTypes(), child);
    }
  }

  public V10GType fetchType(Resource resource, TypeDeclaration typeDeclaration) {

    String key = Names.ramlTypeName(resource, typeDeclaration);
    if (types.containsKey(key)) {

      return types.get(key);
    } else {

      V10GType type =
          createInlineType(key, Names.javaTypeName(resource, typeDeclaration), typeDeclaration, null, CreationModel.NEVER_INLINE);

      storeNewType(type);
      return type;
    }
  }

  public V10GType fetchType(Resource resource, Method method, TypeDeclaration typeDeclaration) {

    String key = Names.ramlTypeName(resource, method, typeDeclaration);
    if (types.containsKey(key)) {

      return types.get(key);
    } else {

      V10GType type =
          createInlineType(key, Names.javaTypeName(resource, method, typeDeclaration), typeDeclaration, null,
                           CreationModel.NEVER_INLINE);

      storeNewType(type);
      return type;
    }
  }

  private void storeNewType(V10GType type) {
    if (!type.isScalar()) {
      types.put(type.name(), type);
    }
  }

  public V10GType fetchType(Resource resource, Method method, Response response,
                            TypeDeclaration typeDeclaration) {
    String key = Names.ramlTypeName(resource, method, response, typeDeclaration);
    if (types.containsKey(key)) {

      return types.get(key);
    } else {

      V10GType type =
          V10GTypeFactory.createResponseBodyType(this, resource, method, response, typeDeclaration);
      storeNewType(type);
      return type;
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
              V10GTypeFactory.createEnum(this, name, (StringTypeDeclaration) typeDeclaration, CreationModel.INLINE_FROM_TYPE);
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
            V10GTypeFactory.createUnion(this, (UnionTypeDeclaration) typeDeclaration,
                                        typeDeclaration.name(), CreationModel.INLINE_FROM_TYPE);
      } else {
        type = V10GTypeFactory.createExplicitlyNamedType(this, name, typeDeclaration);
      }
      storeNewType(type);
      return type;
    }
  }

  public V10GType fetchType(TypeDeclaration typeDeclaration) {

    String name = typeDeclaration.name();
    return fetchType(name, typeDeclaration);
  }

  public V10GType createInlineType(String name, String javaTypeName, TypeDeclaration typeDeclaration, V10GType containingType,
                                   CreationModel model) {

    Class<?> javaType = ScalarTypes.scalarToJavaType(typeDeclaration);
    if (javaType != null) {

      if (typeDeclaration instanceof StringTypeDeclaration
          && ((StringTypeDeclaration) typeDeclaration).enumValues().size() > 0) {
        V10GType type =
            V10GTypeFactory.createEnum(this, name, javaTypeName,
                                       (StringTypeDeclaration) typeDeclaration, model);
        storeNewType(type);
        return type;

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
                                       typeDeclaration.name(), javaTypeName, model);
      } else if (typeDeclaration instanceof XMLTypeDeclaration) {
        type =
            V10GTypeFactory.createXml((XMLTypeDeclaration) typeDeclaration, typeDeclaration.name(),
                                      javaTypeName, model);
      } else if (typeDeclaration instanceof UnionTypeDeclaration) {
        type =
            V10GTypeFactory.createUnion(this, (UnionTypeDeclaration) typeDeclaration,
                                        typeDeclaration.name(), javaTypeName, model);
      } else {
        type = V10GTypeFactory.createInlineType(this, name, javaTypeName, typeDeclaration, containingType);
      }
      storeNewType(type);
      return type;
    }
  }

  public Multimap<String, V10GType> getChildClasses() {
    return childMap;
  }

  public V10TypeRegistry createRegistry() {
    V10TypeRegistry registry = new V10TypeRegistry();
    registry.types = new HashMap<>();
    registry.types.putAll(this.types);
    registry.childMap = this.childMap;

    return registry;
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
