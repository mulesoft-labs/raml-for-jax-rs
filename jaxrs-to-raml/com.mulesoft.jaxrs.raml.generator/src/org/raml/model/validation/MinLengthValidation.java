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
package org.raml.model.validation;

/**
 * <p>MinLengthValidation class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class MinLengthValidation implements Validation
{

    private int minLength;

    /**
     * <p>Constructor for MinLengthValidation.</p>
     *
     * @param minLength a int.
     */
    public MinLengthValidation(int minLength)
    {
        this.minLength = minLength;
    }

    
    /** {@inheritDoc} */
    public boolean check(String input)
    {
        return input.length() >= minLength;
    }
}
