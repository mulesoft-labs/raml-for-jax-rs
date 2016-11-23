package org.raml.model.impl;

import org.raml.model.HttpMethod;

import javax.print.DocFlavor;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpMethodImpl implements HttpMethod {

    private final String string;

    private HttpMethodImpl(String string) {
        this.string = string;
    }

    //TODO: make an enum here too.
    public static HttpMethodImpl create(String string) {
        checkNotNull(string);

        return new HttpMethodImpl(string);
    }

    @Override
    public String getString() {
        return this.string;
    }
}
