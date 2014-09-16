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
package org.raml.parser.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.raml.parser.builder.TupleBuilder;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.rule.TupleRule;

@Retention(value = RetentionPolicy.RUNTIME)
public @interface Sequence
{

    boolean required() default false;

    Class<? extends TupleRule> rule() default TupleRule.class;

    Class<? extends TupleBuilder> builder() default TupleBuilder.class;

    Class<? extends TupleHandler> handler() default TupleHandler.class;

    String alias() default "";
    
    Class<? extends ExtraHandler>extraHandler() default ExtraHandler.class;
}
