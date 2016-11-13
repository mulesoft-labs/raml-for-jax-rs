package org.raml.jaxrs.generator;

import com.google.common.collect.ImmutableMap;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/4/16.
 * Just potential zeroes and ones
 */
public class ScalarTypes {


    private static Map<String, Class<?>> scalarToType = ImmutableMap.<String, Class<?>>builder()
            .put("integer", int.class)
            .put("boolean", boolean.class)
            .put("datetime", Date.class)
            .put("number", BigDecimal.class)
            .put("any", Object.class)
            .put("string", String.class)
            .put("file", File.class).build();

    public static Class<?> scalarToJavaType(String name) {

        String s = name.toLowerCase();
        Class<?> clss = scalarToType.get(s);
        return clss;
    }
}
