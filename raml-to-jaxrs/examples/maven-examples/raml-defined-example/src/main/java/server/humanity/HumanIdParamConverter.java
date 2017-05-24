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
package server.humanity;

import example.model.HumanId;
import example.model.HumanIdImpl;

import javax.ws.rs.ext.ParamConverter;

/**
 * @author JP
 */
public class HumanIdParamConverter implements ParamConverter<HumanId> {

  @Override
  public HumanId fromString(String value) {

    String[] values = value.split("-", 2);
    HumanId id = new HumanIdImpl();
    id.setType(values[0]);
    id.setSerial(values[1]);
    return id;
  }

  @Override
  public String toString(HumanId value) {
    return value.getType() + "-" + value.getSerial();
  }
}
