/*
 * Copyright 2013-2017 (c) MuleSoft, Inc.
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
package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.ramltypes.GProperty;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLFacetInfo;

/**
 * Created by Jean-Philippe Belanger on 12/10/16. Just potential zeroes and ones
 */
public class V10GProperty implements GProperty {

    private final TypeDeclaration input;
    private final GType type;

    public V10GProperty(TypeDeclaration input, GType type) {
        this.input = input;
        this.type = type;
    }

    @Override
    public TypeDeclaration implementation() {
        return input;
    }

    public XMLFacetInfo xml() {

        if (input.xml() == null) {
            return new NullXMLFacetInfo();
        }
        return input.xml();
    }

    @Override
    public String name() {
        return input.name();
    }

    @Override
    public GType type() {
        return type;
    }

    @Override
    public boolean isInline() {
        return TypeUtils.shouldCreateNewClass(input, input.parentTypes().toArray(new TypeDeclaration[0]));
    }

    @Override
    public GProperty overrideType(GType type) {

        return new V10GProperty(input, type);
    }

    @Override
    public String getDefaultValue() {
        return input.defaultValue();
    }

    @Override
    public String toString() {
        return "V10GProperty{" + "name=" + input.name() + ", type=" + type + '}';
    }

}
