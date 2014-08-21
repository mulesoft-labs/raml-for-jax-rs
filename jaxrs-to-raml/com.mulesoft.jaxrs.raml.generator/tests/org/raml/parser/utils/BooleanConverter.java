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
/**
 * 
 */

package org.raml.parser.utils;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class BooleanConverter implements Converter
{

    /*
     * (non-Javadoc)
     * @see org.apache.commons.beanutils.Converter#convert(java.lang.Class,
     * java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object convert(Class type, Object value)
    {
        if (value instanceof Boolean)
        {
            return (value);
        }
        try
        {
            String stringValue = value.toString();
            if (stringValue.equalsIgnoreCase("yes") || stringValue.equals("y")
                || stringValue.equalsIgnoreCase("true") || stringValue.equals("t"))
            {
                return (Boolean.TRUE);
            }
            else if (stringValue.equalsIgnoreCase("no") || stringValue.equals("n")
                     || stringValue.equalsIgnoreCase("false") || stringValue.equalsIgnoreCase("f"))
            {
                return (Boolean.FALSE);
            }
            else
            {
                throw new ConversionException(stringValue);
            }
        }
        catch (ClassCastException e)
        {
            throw new ConversionException(e);
        }
    }
}
