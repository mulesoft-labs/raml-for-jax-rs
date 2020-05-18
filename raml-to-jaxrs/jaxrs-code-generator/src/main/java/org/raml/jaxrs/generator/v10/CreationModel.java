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
package org.raml.jaxrs.generator.v10;

import amf.client.model.domain.AnyShape;
import org.raml.ramltopojo.amf.ExtraInformation;

/**
 * Created by jpbelang on 2017-06-17.
 */
public enum CreationModel {

  INLINE_FROM_TYPE {

    @Override
    public boolean isInline(AnyShape anyShape) {

      if (anyShape == null) {
        return false;
      } else {
        // todo hide this
        return ExtraInformation.extraInformation().isInline(anyShape);
      }
    }
  };

  public abstract boolean isInline(AnyShape t);
}
