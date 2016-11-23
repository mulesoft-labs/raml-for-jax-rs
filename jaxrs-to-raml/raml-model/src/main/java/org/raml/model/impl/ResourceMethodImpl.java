package org.raml.model.impl;

import com.google.common.collect.ImmutableList;

import org.raml.model.MediaType;
import org.raml.model.ResourceMethod;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ResourceMethodImpl implements ResourceMethod {

    private final String string;
    private final ImmutableList<MediaType> consumedTypes;

    private ResourceMethodImpl(String string, ImmutableList<MediaType> consumedTypes) {
        this.string = string;
        this.consumedTypes = consumedTypes;
    }

    //TODO: make an enum here too.
    public static ResourceMethodImpl create(String string, Iterable<MediaType> consumedTypes) {
        checkNotNull(string);

        return new ResourceMethodImpl(string, ImmutableList.copyOf(consumedTypes));
    }

    @Override
    public String getString() {
        return this.string;
    }

    @Override
    public List<MediaType> getConsumedMediaTypes() {
        return this.consumedTypes;
    }
}
