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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

/**
 * <p>Abstract Constants class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public abstract class Constants
{
    /** Constant <code>JAVA_KEYWORDS</code> */
    public static final Set<String> JAVA_KEYWORDS = Collections.unmodifiableSet(new HashSet<String>(
        Arrays.asList("abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "do", "double", "else", "enum", "extends", "false", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "null", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw",
            "throws", "transient", "true", "try", "void", "volatile", "while")));

    /** Constant <code>JAXRS_HTTP_METHODS</code> */
    @SuppressWarnings("unchecked")
    public static final List<Class<? extends Annotation>> JAXRS_HTTP_METHODS = Arrays.asList(DELETE.class,
        GET.class, HEAD.class, OPTIONS.class, POST.class, PUT.class);

    /** Constant <code>DEFAULT_LOCALE</code> */
    public static Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /** Constant <code>RESPONSE_HEADER_WILDCARD_SYMBOL="{?}"</code> */
    public static final String RESPONSE_HEADER_WILDCARD_SYMBOL = "{?}";

    private Constants()
    {
        throw new UnsupportedOperationException();
    }
}
