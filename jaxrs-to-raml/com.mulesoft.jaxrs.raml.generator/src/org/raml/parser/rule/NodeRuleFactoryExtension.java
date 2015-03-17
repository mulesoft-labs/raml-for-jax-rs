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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * <p>NodeRuleFactoryExtension interface.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public interface NodeRuleFactoryExtension
{

    /**
     * <p>handles.</p>
     *
     * @param field a {@link java.lang.reflect.Field} object.
     * @param annotation a {@link java.lang.annotation.Annotation} object.
     * @return a boolean.
     */
    boolean handles(Field field, Annotation annotation);

    /**
     * <p>createRule.</p>
     *
     * @param field a {@link java.lang.reflect.Field} object.
     * @param annotation a {@link java.lang.annotation.Annotation} object.
     * @return a {@link org.raml.parser.rule.TupleRule} object.
     */
    TupleRule<?,?> createRule(Field field,Annotation annotation);

}
