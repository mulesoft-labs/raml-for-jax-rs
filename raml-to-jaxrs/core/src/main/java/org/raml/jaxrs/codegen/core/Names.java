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

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.StringUtils.remove;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.apache.commons.lang.WordUtils.capitalize;
import static org.apache.commons.lang.math.NumberUtils.isDigits;
import static org.raml.jaxrs.codegen.core.Constants.DEFAULT_LOCALE;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.raml.model.Action;
import org.raml.model.MimeType;
import org.raml.model.Resource;

/**
 * <p>Names class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Names
{
    /** Constant <code>GENERIC_PAYLOAD_ARGUMENT_NAME="entity"</code> */
    public static final String GENERIC_PAYLOAD_ARGUMENT_NAME = "entity";
    /** Constant <code>MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME="headers"</code> */
    public static final String MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME = "headers";
    /** Constant <code>EXAMPLE_PREFIX=" e.g. "</code> */
    public static final String EXAMPLE_PREFIX = " e.g. ";

    /**
     * <p>buildResourceInterfaceName.</p>
     *
     * @param resource a {@link org.raml.model.Resource} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildResourceInterfaceName(final Resource resource)
    {
        final String resourceInterfaceName = buildJavaFriendlyName(defaultIfBlank(resource.getDisplayName(),
            resource.getRelativeUri()));

        return isBlank(resourceInterfaceName) ? "Root" : resourceInterfaceName.concat("Resource");
    }

    /**
     * <p>buildVariableName.</p>
     *
     * @param source a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildVariableName(final String source)
    {
        final String name = uncapitalize(buildJavaFriendlyName(source));

        return Constants.JAVA_KEYWORDS.contains(name) ? "$" + name : name;
    }

    /**
     * <p>buildJavaFriendlyName.</p>
     *
     * @param source a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildJavaFriendlyName(final String source)
    {
        final String baseName = source.replaceAll("[\\W_]", " ");

        String friendlyName = capitalize(baseName).replaceAll("[\\W_]", "");

        if (isDigits(left(friendlyName, 1)))
        {
            friendlyName = "_" + friendlyName;
        }

        return friendlyName;
    }

    /**
     * <p>buildResourceMethodName.</p>
     *
     * @param action a {@link org.raml.model.Action} object.
     * @param bodyMimeType a {@link org.raml.model.MimeType} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildResourceMethodName(final Action action, final MimeType bodyMimeType)
    {
        final String methodBaseName = buildJavaFriendlyName(action.getResource()
            .getUri()
            .replace("{", " By "));

        return action.getType().toString().toLowerCase() + buildMimeTypeInfix(bodyMimeType) + methodBaseName;
    }

    /**
     * <p>buildResponseMethodName.</p>
     *
     * @param statusCode a int.
     * @param mimeType a {@link org.raml.model.MimeType} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildResponseMethodName(final int statusCode, final MimeType mimeType)
    {
        final String status = EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, DEFAULT_LOCALE);
        
       String string = getShortMimeType(mimeType)
		                            + buildJavaFriendlyName(defaultIfBlank(status, "_" + statusCode));
       return "with" + Character.toUpperCase(string.charAt(0))+string.substring(1);
    }

    /**
     * <p>buildNestedSchemaName.</p>
     *
     * @param mimeType a {@link org.raml.model.MimeType} object.
     * @return a {@link java.lang.String} object.
     */
    public static String buildNestedSchemaName(final MimeType mimeType)
    {
        // TODO improve naming strategy for nested schemas
        return getShortMimeType(mimeType)
               + (isBlank(mimeType.getSchema()) ? mimeType.hashCode() : mimeType.getSchema().hashCode());
    }

    private static String buildMimeTypeInfix(final MimeType bodyMimeType)
    {
        return bodyMimeType != null ? buildJavaFriendlyName(getShortMimeType(bodyMimeType)) : "";
    }

    /**
     * <p>getShortMimeType.</p>
     *
     * @param mimeType a {@link org.raml.model.MimeType} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getShortMimeType(final MimeType mimeType)
    {
		if (mimeType == null) {
			return "";
		}
		String subType = StringUtils.substringAfter(mimeType.getType()
				.toLowerCase(DEFAULT_LOCALE), "/");

		if (subType.contains(".")) {
			// handle types like application/vnd.example.v1+json
			StringBuilder sb = new StringBuilder();
			for (String s : subType.split("\\W+")) {
				sb.append(sb.length() == 0 ? s : StringUtils.capitalize(s));
			}
			return sb.toString();
		} else {
			// handle any other types
			return remove(remove(remove(subType, "x-www-"), "+"), "-");
		}

    }

    private Names()
    {
        throw new UnsupportedOperationException();
    }
}
