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
package org.raml.emitter;

import com.google.common.base.Optional;
import org.junit.Test;
import org.raml.api.*;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by barnabef on 3/27/17.
 */
public class IndentedAppendableEmitterTest {

    private IndentedAppendable mockAppendable = mock(IndentedAppendable.class);
    ;

    @Test
    public void testCanCreateRAML() throws RamlEmissionException, IOException {
        RamlApi mockRamlApi = createRamlApi("This is a test", "1.0", "http://www.test.com/test", "application/json")
                .withResource(createRamlResource("/root")
                        .withMethod(createRamlMethod("put", "This is a description with \"double quotes\" but without control character")
                                .withConsumedMediaType(createMediaType("application/*"))
                                .withProducedMediaType(createMediaType("application/jsonv2"))
                        )
                );

//     StringWriter appendable = new StringWriter();
//     IndentedAppendableEmitter emitter = IndentedAppendableEmitter.create(IndentedAppendable.forNoSpaces(2, appendable));
        IndentedAppendableEmitter emitter = IndentedAppendableEmitter.create(mockAppendable);

        emitter.emit(mockRamlApi);
//     System.out.println(appendable.toString());

        verify(mockAppendable).appendLine("#%RAML 1.0");
        verify(mockAppendable).appendEscapedLine("title", "This is a test");
        verify(mockAppendable).appendEscapedLine("version", "1.0");
        verify(mockAppendable).appendEscapedLine("baseUri", "http://www.test.com/test");
        verify(mockAppendable).appendEscapedLine("mediaType", "application/json");
        verify(mockAppendable).appendLine("/root:");
        verify(mockAppendable).appendLine("put:");
        verify(mockAppendable).appendEscapedLine("description", "This is a description with \"double quotes\" but without control character");
        verify(mockAppendable, times(2)).appendLine("body:");
        verify(mockAppendable).appendLine("application/*:");
        verify(mockAppendable).appendLine("responses:");
        verify(mockAppendable).appendLine("application/jsonv2:");
    }

    private TestRamlApi createRamlApi(String title, String version, String baseUrl, String defaultMediaType) {
        return new TestRamlApi(title, version, baseUrl, defaultMediaType);
    }

    private TestRamlResource createRamlResource(String path) {
        return new TestRamlResource(path);
    }

    private TestRamlResourceMethod createRamlMethod(String method, String description) {
        return new TestRamlResourceMethod(method, description);
    }

    private TestRamlMediaType createMediaType(String mediaType) {
        return new TestRamlMediaType(mediaType);
    }

    class TestRamlResource implements RamlResource {

        private String path;
        private ArrayList<RamlResourceMethod> methods = new ArrayList<>();
        private ArrayList<RamlResource> children = new ArrayList<>();

        public TestRamlResource(String path) {
            this.path = path;
        }

        @Override
        public String getPath() {
            return path;
        }

        @Override
        public List<RamlResource> getChildren() {
            return children;
        }

        @Override
        public List<RamlResourceMethod> getMethods() {
            return methods;
        }

        public TestRamlResource withMethod(TestRamlResourceMethod method) {
            methods.add(method);
            return this;
        }
    }

    class TestRamlMediaType implements RamlMediaType {

        String mediaType;

        public TestRamlMediaType(String mediaType) {
            this.mediaType = mediaType;
        }

        @Override
        public String toStringRepresentation() {
            return mediaType;
        }
    }

    class TestRamlApi implements RamlApi {

        private String title;
        private String version;
        private String baseUrl;
        private RamlMediaType defaultMediaType;
        private List<RamlResource> ramlResources = new ArrayList<>();

        private TestRamlApi(String title, String version, String baseUrl, String defaultMediaType) {
            this.title = title;
            this.version = version;
            this.baseUrl = baseUrl;
            this.defaultMediaType = new TestRamlMediaType(defaultMediaType);
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public String getVersion() {
            return version;
        }

        @Override
        public String getBaseUri() {
            return baseUrl;
        }

        @Override
        public List<RamlResource> getResources() {
            return ramlResources;
        }

        @Override
        public RamlMediaType getDefaultMediaType() {
            return defaultMediaType;
        }

        public TestRamlApi withResource(TestRamlResource resource) {
            ramlResources.add(resource);
            return this;
        }

    }

    private class TestRamlResourceMethod implements RamlResourceMethod {

        private ArrayList<RamlMediaType> producedMediaTypes = new ArrayList<>();
        private ArrayList<RamlQueryParameter> queryParameters = new ArrayList<>();
        private ArrayList<RamlHeaderParameter> headerParameters = new ArrayList<>();
        private Optional<String> description = Optional.absent();
        private Optional<Type> producedType = Optional.of((Type) String.class);
        private Optional<Type> consumedType = Optional.of((Type) String.class);
        private ArrayList<RamlMediaType> consumedMediaTypes = new ArrayList<>();
        private String method;

        public TestRamlResourceMethod(String method, String description) {
            this.method = method;
            this.description = Optional.fromNullable(description);
        }

        @Override
        public String getHttpMethod() {
            return method;
        }

        @Override
        public List<RamlMediaType> getConsumedMediaTypes() {
            return consumedMediaTypes;
        }

        @Override
        public List<RamlMediaType> getProducedMediaTypes() {
            return producedMediaTypes;
        }

        @Override
        public List<RamlQueryParameter> getQueryParameters() {
            return queryParameters;
        }

        @Override
        public List<RamlHeaderParameter> getHeaderParameters() {
            return headerParameters;
        }

        @Override
        public Optional<String> getDescription() {
            return description;
        }

        @Override
        public Optional<Type> getConsumedType() {
            return consumedType;
        }

        @Override
        public Optional<Type> getProducedType() {
            return producedType;
        }

        public TestRamlResourceMethod withConsumedMediaType(TestRamlMediaType mediaType) {
            consumedMediaTypes.add(mediaType);
            return this;
        }

        public TestRamlResourceMethod withProducedMediaType(TestRamlMediaType mediaType) {
            producedMediaTypes.add(mediaType);
            return this;
        }
    }
}
