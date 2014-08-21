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

public final class ValidationMessage
{

    public static final String NON_SCALAR_KEY_MESSAGE = "Only scalar keys are allowed";

    private static final String EMPTY_MESSAGE = "can not be empty";
    private static final String DUPLICATE_MESSAGE = "Duplicate";
    private static final String TYPE_MISMATCH_MESSAGE = "Type mismatch: ";
    private static final String IS_MISSING = "is missing";

    private ValidationMessage()
    {
    }

    public static String getRuleEmptyMessage(String ruleName)
    {
        return ruleName + " " + EMPTY_MESSAGE;
    }

    public static String getDuplicateRuleMessage(String ruleName)
    {
        return DUPLICATE_MESSAGE + " " + ruleName;
    }


    public static String getRuleTypeMisMatch(String name, String fieldType)
    {
        return TYPE_MISMATCH_MESSAGE + name + " must be of type " + fieldType;
    }

    public static String getMissingRuleMessage(String ruleName)
    {
        return ruleName + " " + IS_MISSING;
    }
}
