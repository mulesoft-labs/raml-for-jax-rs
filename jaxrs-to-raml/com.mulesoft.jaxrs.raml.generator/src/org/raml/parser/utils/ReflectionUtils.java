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
package org.raml.parser.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtilsBean;

/**
 * <p>ReflectionUtils class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class ReflectionUtils
{

    private static final Set<Class<?>> WRAPPERS_PLUS_STRING = new HashSet<Class<?>>();

    static
    {
        WRAPPERS_PLUS_STRING.add(Boolean.class);
        WRAPPERS_PLUS_STRING.add(Character.class);
        WRAPPERS_PLUS_STRING.add(Byte.class);
        WRAPPERS_PLUS_STRING.add(Short.class);
        WRAPPERS_PLUS_STRING.add(Integer.class);
        WRAPPERS_PLUS_STRING.add(Long.class);
        WRAPPERS_PLUS_STRING.add(Float.class);
        WRAPPERS_PLUS_STRING.add(Double.class);
        WRAPPERS_PLUS_STRING.add(BigInteger.class);
        WRAPPERS_PLUS_STRING.add(BigDecimal.class);
        WRAPPERS_PLUS_STRING.add(String.class);
    }

    /**
     * <p>isWrapperOrString.</p>
     *
     * @param type a {@link java.lang.Class} object.
     * @return a boolean.
     */
    public static boolean isWrapperOrString(Class<?> type)
    {
        return WRAPPERS_PLUS_STRING.contains(type);
    }

    /**
     * <p>getInheritedFields.</p>
     *
     * @param type a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Field> getInheritedFields(Class<?> type)
    {
        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = type; c != null; c = c.getSuperclass())
        {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fields;
    }

    /**
     * <p>setProperty.</p>
     *
     * @param parent a {@link java.lang.Object} object.
     * @param fieldName a {@link java.lang.String} object.
     * @param value a {@link java.lang.Object} object.
     */
    @SuppressWarnings("unchecked")
    public static void setProperty(Object parent, String fieldName, Object value)
    {
        if (parent instanceof List)
        {
            ((List) parent).add(value);
        }
        else if (parent instanceof Map)
        {
            ((Map) parent).put(fieldName, value);
        }
        else
        {
            try
            {
                new PropertyUtilsBean().setProperty(parent, fieldName, value);
            }
            catch (IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
            catch (NoSuchMethodException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * <p>isPojo.</p>
     *
     * @param type a {@link java.lang.Class} object.
     * @return a boolean.
     */
    public static boolean isPojo(Class<?> type)
    {
        return !(isWrapperOrString(type) || isEnum(type) || type.isPrimitive());
    }

    /**
     * <p>isEnum.</p>
     *
     * @param type a {@link java.lang.Class} object.
     * @return a boolean.
     */
    public static boolean isEnum(Class<?> type)
    {
        return type.isEnum() || (type.getSuperclass() != null && type.getSuperclass().isEnum());
    }
}
