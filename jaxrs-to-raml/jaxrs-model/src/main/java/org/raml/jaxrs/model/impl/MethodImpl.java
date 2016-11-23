package org.raml.jaxrs.model.impl;

import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.HttpVerb;

import static com.google.common.base.Preconditions.checkNotNull;

public class MethodImpl implements Method {

    private final HttpVerb httpVerb;

    private MethodImpl(HttpVerb httpVerb) {
        this.httpVerb = httpVerb;
    }

    public static MethodImpl create(HttpVerb httpVerb) {
        checkNotNull(httpVerb);

        return new MethodImpl(httpVerb);
    }

    @Override
    public HttpVerb getHttpVerb() {
        return this.httpVerb;
    }
}
