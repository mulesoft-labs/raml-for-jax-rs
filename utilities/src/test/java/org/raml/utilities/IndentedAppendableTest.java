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
package org.raml.utilities;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by barnabef on 3/31/17.
 */
public class IndentedAppendableTest {

  @Test
  public void testEscaping() {
    assertStringIsCorrectlyEscaped("title", "[This is a test]", "title: \"[This is a test]\"");
    assertStringIsCorrectlyEscaped("version", "#1.0", "version: \"#1.0\"");
    assertStringIsCorrectlyEscaped("baseUri", "http://www.test.com/test?test=true#hash",
                                   "baseUri: \"http://www.test.com/test?test=true#hash\""); // No quotes if not first character
    assertStringIsCorrectlyEscaped("mediaType", "*/*", "mediaType: \"*/*\"");
    assertStringIsCorrectlyEscaped("description", "|This is a description with \"double quotes\" but with control character",
                                   "description: \"|This is a description with \\\"double quotes\\\" but with control character\"");
    assertStringIsCorrectlyEscaped("description",
                                   "This is a description with lots of characters that are not escaped *|#{}?,[]",
                                   "description: This is a description with lots of characters that are not escaped *|#{}?,[]");
    assertStringIsCorrectlyEscaped("description", "{This is a description with control character}",
                                   "description: \"{This is a description with control character}\"");
    assertStringIsCorrectlyEscaped("description", "[This is a description with control character]",
                                   "description: \"[This is a description with control character]\"");
    assertStringIsCorrectlyEscaped("description", "- text", "description: \"- text\"");
    assertStringIsCorrectlyEscaped("description", "? text", "description: \"? text\"");
    assertStringIsCorrectlyEscaped("description", "& text", "description: \"& text\"");
    assertStringIsCorrectlyEscaped("description", "! text", "description: \"! text\"");
    assertStringIsCorrectlyEscaped("description", "> text", "description: \"> text\"");
    assertStringIsCorrectlyEscaped("description", "' text", "description: \"' text\"");
    assertStringIsCorrectlyEscaped("description", ": text", "description: \": text\"");
    assertStringIsCorrectlyEscaped("description", "% text", "description: \"% text\"");
    assertStringIsCorrectlyEscaped("description", "@ text", "description: \"@ text\"");
    assertStringIsCorrectlyEscaped("description", "` text", "description: \"` text\"");
    assertStringIsCorrectlyEscaped("description", ", text", "description: \", text\"");
    assertStringIsCorrectlyEscaped("description", "\" text\"", "description: \"\\\" text\\\"\"");
    assertStringIsCorrectlyEscaped("description", "something: xxx", "description: \"something: xxx\"");

  }

  @Test
  public void testAppendList() {
    StringBuffer buffer = new StringBuffer();
    IndentedAppendable appendable = IndentedAppendable.forNoSpaces(1, buffer);
    try {
      appendable.appendList("test", "text", "text2", "#text", "te,xt");
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assert.assertEquals("test: [text, text2, \"#text\", \"te,xt\"]", buffer.toString().trim());

  }

  public void assertStringIsCorrectlyEscaped(String tag, String value, String expected) {
    StringBuffer buffer = new StringBuffer();
    IndentedAppendable appendable = IndentedAppendable.forNoSpaces(1, buffer);
    try {
      appendable.appendEscapedLine(tag, value);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assert.assertEquals(expected, buffer.toString().trim());
  }
}
