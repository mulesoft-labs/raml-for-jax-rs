/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.parser.rule;

/**
 * <p>ValidationMessage class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public final class ValidationMessage
{

    /** Constant <code>NON_SCALAR_KEY_MESSAGE="Only scalar keys are allowed"</code> */
    public static final String NON_SCALAR_KEY_MESSAGE = "Only scalar keys are allowed";

    private static final String EMPTY_MESSAGE = "can not be empty";
    private static final String DUPLICATE_MESSAGE = "Duplicate";
    private static final String TYPE_MISMATCH_MESSAGE = "Type mismatch: ";
    private static final String IS_MISSING = "is missing";

    private ValidationMessage()
    {
    }

    /**
     * <p>getRuleEmptyMessage.</p>
     *
     * @param ruleName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getRuleEmptyMessage(String ruleName)
    {
        return ruleName + " " + EMPTY_MESSAGE;
    }

    /**
     * <p>getDuplicateRuleMessage.</p>
     *
     * @param ruleName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getDuplicateRuleMessage(String ruleName)
    {
        return DUPLICATE_MESSAGE + " " + ruleName;
    }


    /**
     * <p>getRuleTypeMisMatch.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param fieldType a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getRuleTypeMisMatch(String name, String fieldType)
    {
        return TYPE_MISMATCH_MESSAGE + name + " must be of type " + fieldType;
    }

    /**
     * <p>getMissingRuleMessage.</p>
     *
     * @param ruleName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getMissingRuleMessage(String ruleName)
    {
        return ruleName + " " + IS_MISSING;
    }
}
