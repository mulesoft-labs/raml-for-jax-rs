/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
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

package org.raml.jaxrs.codegen.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.aml.apimodel.MimeType;
import org.junit.Test;

public class NamesTestCase
{
    @Test
    public void getShortMimeType()
    {
        assertThat(Names.getShortMimeType(null), is(""));//FIXME
//        assertThat(Names.getShortMimeType(new MimeType("text/xml")), is("xml"));
//        assertThat(Names.getShortMimeType(new MimeType("application/json")), is("json"));
//        assertThat(Names.getShortMimeType(new MimeType("application/hal+json")), is("haljson"));
//        assertThat(Names.getShortMimeType(new MimeType("application/octet-stream")), is("octetstream"));
//        assertThat(Names.getShortMimeType(new MimeType("application/x-www-form-urlencoded")), is("formurlencoded"));
//        assertThat(Names.getShortMimeType(new MimeType("application/vnd.example.v1+json")), is("vndExampleV1Json"));
    }
}