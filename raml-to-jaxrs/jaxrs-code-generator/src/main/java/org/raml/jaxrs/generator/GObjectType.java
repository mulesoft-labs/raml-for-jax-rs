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

/**
 * Created by Jean-Philippe Belanger on 12/7/16. Just potential zeroes and ones
 */
public enum GObjectType implements GeneratorObjectType {

  PLAIN_OBJECT_TYPE {

    @Override
    public void dispatch(GObjectTypeDispatcher dispatcher) {

      dispatcher.onPlainObject();
    }
  },
  UNION_TYPE {

    @Override
    public void dispatch(GObjectTypeDispatcher dispatcher) {

      dispatcher.onUnion();
    }
  },
  XML_OBJECT_TYPE {

    @Override
    public void dispatch(GObjectTypeDispatcher dispatcher) {
      dispatcher.onXmlObject();
    }
  },
  JSON_OBJECT_TYPE {

    @Override
    public void dispatch(GObjectTypeDispatcher dispatcher) {
      dispatcher.onJsonObject();
    }
  },
  SCALAR {

    @Override
    public void dispatch(GObjectTypeDispatcher dispatcher) {
      throw new GenerationException("scalar object cannot be handled");
    }
  },
  ENUMERATION_TYPE {

    @Override
    public void dispatch(GObjectTypeDispatcher dispatcher) {
      dispatcher.onEnumeration();
    }
  };

  public static class GObjectTypeDispatcher {

    public void onPlainObject() {};

    public void onXmlObject() {};

    public void onJsonObject() {};

    public void onEnumeration() {};

    public void onUnion() {};
  }

  public abstract void dispatch(GObjectTypeDispatcher dispatcher);
}
