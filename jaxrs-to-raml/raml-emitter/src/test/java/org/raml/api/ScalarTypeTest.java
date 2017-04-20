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
package org.raml.api;

import com.google.common.base.Optional;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.junit.Test;

import java.io.InputStream;
import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 4/9/17. Just potential zeroes and ones
 */
public class ScalarTypeTest {

  @Test
  public void numeric() {

    Optional<ScalarType> intType = ScalarType.fromType(int.class);
    assertEquals("integer", intType.get().getRamlSyntax());

    Optional<ScalarType> oIntType = ScalarType.fromType(Integer.class);
    assertEquals("integer", oIntType.get().getRamlSyntax());

    Optional<ScalarType> bigIntType = ScalarType.fromType(BigInteger.class);
    assertEquals("integer", bigIntType.get().getRamlSyntax());

    Optional<ScalarType> doubleType = ScalarType.fromType(double.class);
    assertEquals("number", doubleType.get().getRamlSyntax());
  }

  @Test
  public void file() {

    Optional<ScalarType> fileType = ScalarType.fromType(InputStream.class);
    assertEquals("file", fileType.get().getRamlSyntax());

    Optional<ScalarType> fileTypeBodyPart = ScalarType.fromType(FileDataBodyPart.class);
    assertEquals("file", fileTypeBodyPart.get().getRamlSyntax());

    Optional<ScalarType> streamTypeBodyPart = ScalarType.fromType(StreamDataBodyPart.class);
    assertEquals("file", streamTypeBodyPart.get().getRamlSyntax());

  }

}
