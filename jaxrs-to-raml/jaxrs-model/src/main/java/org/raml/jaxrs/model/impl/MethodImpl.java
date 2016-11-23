package org.raml.jaxrs.model.impl;

import com.google.common.collect.ImmutableList;

import org.raml.jaxrs.model.Method;
import org.raml.jaxrs.model.HttpVerb;

import java.util.List;

import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkNotNull;

public class MethodImpl implements Method {

    private final HttpVerb httpVerb;
    private final ImmutableList<MediaType> mediaTypes;

    private MethodImpl(HttpVerb httpVerb, ImmutableList<MediaType> mediaTypes) {
        this.httpVerb = httpVerb;
        this.mediaTypes = mediaTypes;
    }

    public static MethodImpl create(HttpVerb httpVerb, Iterable<MediaType> consumedTypes) {
        checkNotNull(httpVerb);

        return new MethodImpl(httpVerb, ImmutableList.copyOf(consumedTypes));
    }

    @Override
    public HttpVerb getHttpVerb() {
        return this.httpVerb;
    }

    @Override
    public List<MediaType> getConsumedMediaTypes() {
        return this.mediaTypes;
    }
}
