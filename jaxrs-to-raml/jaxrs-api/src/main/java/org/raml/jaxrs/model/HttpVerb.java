package org.raml.jaxrs.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import com.sun.scenario.effect.impl.state.HVSeparableKernel;

import java.util.Map;
import java.util.NoSuchElementException;

import static java.lang.String.format;

public enum HttpVerb {
    GET("GET"),
    PUT("PUT"),
    POST("POST"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    PATCH("PATCH"),
    DELETE("DELETE");

    private static final ImmutableMap<String, HttpVerb> VERBS_BY_STRINGS;

    static {
        ImmutableMap.Builder<String, HttpVerb> tempMap = ImmutableMap.builder();

        for (HttpVerb verb : HttpVerb.values()) {
            tempMap.put(verb.getString(), verb);
        }

        VERBS_BY_STRINGS = tempMap.build();
    }

    private final String string;

    HttpVerb(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public static HttpVerb fromString(String httpMethod) {
        HttpVerb verb = VERBS_BY_STRINGS.get(httpMethod);

        if (null == verb) {
            throw new NoSuchElementException(format("unknown http method: %s", httpMethod));
        }

        return verb;
    }
}
