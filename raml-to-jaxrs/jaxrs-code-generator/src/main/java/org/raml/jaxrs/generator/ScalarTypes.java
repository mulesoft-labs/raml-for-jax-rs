package org.raml.jaxrs.generator;

import com.google.common.collect.ImmutableMap;
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

    public static Class<?> scalarToJavaType(TypeDeclaration name) {

        //String s = name.type().toLowerCase();
        for (Class<?> aClass : name.getClass().getInterfaces()) {

            if ( TypeDeclaration.class.isAssignableFrom(aClass)) {

                Class<?> clss = scalarToType.get(aClass);
                return clss;
            }
        }

        return null;
    }

    public static boolean extendsScalarRamlType(TypeDeclaration typeDeclaration) {

        return /*typeDeclaration.type().equals("object") ||*/ scalarToJavaType(typeDeclaration) != null;
    }
}
