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

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.resolver.DefaultTupleHandler;
import org.yaml.snakeyaml.nodes.Node;

public class UnknownTupleRule<K extends Node, V extends Node> extends DefaultTupleRule<K, V>
{

    public UnknownTupleRule(String fieldName)
    {
        super(fieldName, new DefaultTupleHandler());
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {       
        final List<ValidationResult> result = new ArrayList<ValidationResult>();
        if (getKey() != null)
        {
            result.add(ValidationResult.createErrorResult("Unknown key: "+ getName().replaceAll("(.*value=?)(\\w+)(.*)", "$2"),getKey().getStartMark() , getKey().getEndMark()));
        }
        else
        {
            //error already reported as invalid key type
        }
        return result;
    }
    
}
