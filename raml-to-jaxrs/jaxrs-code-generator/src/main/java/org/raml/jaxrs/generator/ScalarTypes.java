package org.raml.jaxrs.generator;

import com.google.common.collect.ImmutableMap;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTimeOnlyTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.FileTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/4/16.
 * Just potential zeroes and ones
 */
public class ScalarTypes {


    private static Map<Class, Class<?>> scalarToType = ImmutableMap.<Class, Class<?>>builder()
            .put(IntegerTypeDeclaration.class, int.class)
            .put(BooleanTypeDeclaration.class, boolean.class)
            .put(DateTimeOnlyTypeDeclaration.class, Date.class)
            .put(DateTypeDeclaration.class, Date.class)
            .put(NumberTypeDeclaration.class, BigDecimal.class)
            .put(StringTypeDeclaration.class, String.class)
            .put(FileTypeDeclaration.class, File.class).build();

    private static Map<String, Class<?>> stringScalarToType = ImmutableMap.<String, Class<?>>builder()
            .put("integer", int.class)
            .put("boolean", boolean.class)
            .put("date-time", Date.class)
            .put("date", Date.class)
            .put("number", BigDecimal.class)
            .put("string", String.class)
            .put("file", File.class).build();

    public static Class<?> scalarToJavaType(TypeDeclaration type) {

        return scalarToType.get(type.getClass().getInterfaces()[0]);
    }

    public static Class<?> scalarToJavaType(String name) {

        return stringScalarToType.get(name.toLowerCase());
    }

    public static Class<?> scalarToJavaType(GType type) {

        if ( type instanceof V10GType) {

            return scalarToJavaType((TypeDeclaration) type.implementation());
        } else {

            return scalarToJavaType(type.name());
        }
    }

    public static boolean extendsScalarRamlType(TypeDeclaration typeDeclaration) {

        return /*typeDeclaration.type().equals("object") ||*/ scalarToJavaType(typeDeclaration.name()) != null;
    }

}
