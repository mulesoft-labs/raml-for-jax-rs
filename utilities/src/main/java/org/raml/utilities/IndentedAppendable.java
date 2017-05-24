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

import com.google.common.base.Strings;

import net.jcip.annotations.NotThreadSafe;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@NotThreadSafe
public class IndentedAppendable {

  private static final String END_OF_LINE = System.lineSeparator();

  private final String indent;
  private final Appendable appendable;
  private StringBuilder deferredContent = new StringBuilder();
  private Appendable currentAppendable;
  // private boolean isDeferred = false;
  private String currentIndent = "";

  private IndentedAppendable(String indent, Appendable appendable) {
    this.indent = indent;
    this.appendable = appendable;
    currentAppendable = appendable;
  }

  public static IndentedAppendable forNoSpaces(int noSpaces, Appendable appendable) {
    checkArgument(noSpaces >= 0);
    checkNotNull(appendable);

    return new IndentedAppendable(Strings.repeat(" ", noSpaces), appendable);
  }

  public void deferAppends() {
    currentAppendable = deferredContent;
  }

  public void stopDeferAppends() {
    currentAppendable = appendable;
  }

  public void flushDeferredContent() throws IOException {
    appendable.append(deferredContent);
    deferredContent = new StringBuilder();
  }

  public void indent() {
    currentIndent += indent;
  }

  public void outdent() {
    checkState(!currentIndent.isEmpty(), "outdenting one too many times");

    currentIndent = currentIndent.substring(0, currentIndent.length() - indent.length());
  }

  public IndentedAppendable withIndent() throws IOException {
    this.currentAppendable.append(currentIndent);
    return this;
  }

  public IndentedAppendable appendLine(String content) throws IOException {
    this.currentAppendable.append(currentIndent).append(content).append("\n");
    return this;
  }

  public IndentedAppendable appendLine(String tag, String content) throws IOException {
    this.currentAppendable.append(currentIndent).append(tag).append(": ").append(content).append("\n");
    return this;
  }

  public IndentedAppendable appendEscapedLine(String tag, String content) throws IOException {
    return appendLine(tag, quoteIfSpecialCharacter(content));
  }

  public IndentedAppendable appendList(String tag, String... content) throws IOException {
    StringBuffer buffer = new StringBuffer();
    buffer.append("[");
    boolean first = true;
    for (String c : content) {
      if (!first) {
        buffer.append(", ");
      }
      first = false;
      buffer.append(quoteIfSpecialCharacter(c, true));
    }
    buffer.append("]");
    return appendLine(tag, buffer.toString());
  }

  public IndentedAppendable endOfLine() throws IOException {
    this.currentAppendable.append(END_OF_LINE);
    return this;
  }

  public String toString() {
    return this.currentAppendable.toString();
  }

  private String quoteIfSpecialCharacter(String value) {
    return quoteIfSpecialCharacter(value, false);
  }

  private String quoteIfSpecialCharacter(String value, boolean inList) {
    boolean escape = value != null
        && (value.matches("[-*|#{}?&!>':%@`,\\[\\]\"].*") || value.matches(".*:.*"))
        || (inList && value.contains(","));
    if (escape) {
      String result = value.replace("\"", "\\\""); // escape double quotes
      return "\"" + result + "\"";
    }
    return value;
  }
}
