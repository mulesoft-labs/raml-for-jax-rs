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

import java.util.List;

import org.yaml.snakeyaml.nodes.Node;

/**
 *
 */
public interface NodeRule<V extends Node>
{

    /**
     * Validates the given value
     *
     * @param value The value to validate
     * @return
     */
    List<ValidationResult> validateValue(V value);

    /**
     * Called when the ruled was ended to verify all mandatory fields are present
     *
     * @return
     */
    List<ValidationResult> onRuleEnd();
}
