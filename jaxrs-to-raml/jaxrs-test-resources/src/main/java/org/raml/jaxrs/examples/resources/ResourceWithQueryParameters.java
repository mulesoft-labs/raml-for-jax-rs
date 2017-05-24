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
package org.raml.jaxrs.examples.resources;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.ws.Response;

@Path("/left/right/left")
public class ResourceWithQueryParameters {

  @Path("step")
  @POST
  @Consumes("application/json")
  @Produces("application/morejson")
  public String postWithQueryParameters(
                                        @NotNull @QueryParam("version") String version,
                                        @DefaultValue("military") @QueryParam("typeOfStep") String typeOfStep,
                                        @DefaultValue("jack") @Size(min = 8, max = 32) @QueryParam("captainName") String theName,
                                        @QueryParam("ageOfCaptain") @Min(25) @Max(100) int captainAge,
                                        @QueryParam("genderOfCaptain") Gender genderOfCaptain,
                                        String foo) {
    return null;
  }
}
