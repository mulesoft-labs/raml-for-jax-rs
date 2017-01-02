package org.raml.jaxrs.generator;

import com.google.common.collect.ImmutableMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/3/16.
 * Just potential zeroes and ones
 */
public class HTTPMethods {

    private static Map<String, Class<? extends Annotation>> nameToAnnotation = ImmutableMap.of(
            "put", PUT.class,
            "get", GET.class,
            "post", POST.class,
            "delete", DELETE.class
    );
    public static Class<? extends Annotation> methodNameToAnnotation(String name) {

        String s = name.toLowerCase();
        return nameToAnnotation.get(s);
    }
}
