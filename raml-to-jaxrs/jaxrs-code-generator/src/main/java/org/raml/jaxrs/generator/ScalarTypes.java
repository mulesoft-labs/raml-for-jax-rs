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
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

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

    // cheating:  I know I only have one table for floats and ints, but the parser
    // should prevent problems.
    private static Map<String, Class<?>> properType = ImmutableMap.<String, Class<?>>builder()
            .put("float", float.class)
            .put("double", double.class)
            .put("int8", byte.class)
            .put("int16", short.class)
            .put("int32", int.class)
            .put("int64", long.class)
            .put("int", int.class).build();

    private static Map<String, Class<?>> properTypeObject = ImmutableMap.<String, Class<?>>builder()
            .put("float", Float.class)
            .put("double", Double.class)
            .put("int8", Byte.class)
            .put("int16", Short.class)
            .put("int32", Integer.class)
            .put("int64", Long.class)
            .put("int", Integer.class).build();

    public static Class<?> scalarToJavaType(TypeDeclaration type) {

        if ( type instanceof IntegerTypeDeclaration ) {

            return properType(shouldUsePrimitiveType((NumberTypeDeclaration) type) ? int.class: Integer.class, (IntegerTypeDeclaration) type);
        }

        if ( type instanceof NumberTypeDeclaration ) {

            return properType(BigDecimal.class, (NumberTypeDeclaration) type);
        }

        return scalarToType.get(type.getClass().getInterfaces()[0]);
    }

    private static Class<?> properType(Class<?> defaultClass, NumberTypeDeclaration type) {

        if ( type.format() == null ) {

            return defaultClass;
        }

        if ( shouldUsePrimitiveType(type)) {
            return properType.get(type.format());
        } else {

            return properTypeObject.get(type.format());
        }
    }

    private static boolean shouldUsePrimitiveType(NumberTypeDeclaration type) {

        for (AnnotationRef annotationRef : type.annotations()) {
            if ( annotationRef.annotation().name().equals("javaPrimitiveType")) {
                return (boolean) annotationRef.structuredValue().value();
            }
        }

        return type.required();
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

        return scalarToJavaType(typeDeclaration.name()) != null;
    }

}
