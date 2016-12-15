package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.BooleanTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.DateTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/12/16.
 * Just potential zeroes and ones
 */
public class Jsr303Extension extends TypeExtensionHelper {

    @Override
    public void onFieldImplementation(FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {


        addFacetsForAll(typeSpec, typeDeclaration);

        if ( typeDeclaration instanceof NumberTypeDeclaration ) {

            addFacetsForNumbers(typeSpec, (NumberTypeDeclaration) typeDeclaration);
            return;
        }

        if ( typeDeclaration instanceof StringTypeDeclaration ) {

            addFacetsForString(typeSpec, (StringTypeDeclaration) typeDeclaration);
        }

        if ( typeDeclaration instanceof ArrayTypeDeclaration ) {

            addFacetsForArray(typeSpec, (ArrayTypeDeclaration) typeDeclaration);
        }
    }

    private void addFacetsForAll(FieldSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

        if ( typeDeclaration.required() != null && typeDeclaration.required()) {

            typeSpec.addAnnotation(AnnotationSpec.builder(NotNull.class).build());
        }
    }

    private void addFacetsForArray(FieldSpec.Builder typeSpec, ArrayTypeDeclaration typeDeclaration) {
        AnnotationSpec.Builder minMax = null;
        if ( typeDeclaration.minItems() != null ) {

            minMax = AnnotationSpec.builder(Size.class).addMember("min", "$L", typeDeclaration.minItems());
        }

        if ( typeDeclaration.maxItems() != null ) {

            if ( minMax == null ) {
                minMax = AnnotationSpec.builder(Size.class).addMember("max", "$L", typeDeclaration.maxItems());
            } else {

                minMax.addMember("max", "$L", typeDeclaration.maxItems());
            }
        }

        if ( minMax != null ) {
            typeSpec.addAnnotation(minMax.build());
        }
    }

    private void addFacetsForString(FieldSpec.Builder typeSpec, StringTypeDeclaration typeDeclaration) {

        AnnotationSpec.Builder minMax = null;
        if ( typeDeclaration.minLength() != null ) {

            minMax = AnnotationSpec.builder(Size.class).addMember("min", "$L", typeDeclaration.minLength());
        }

        if ( typeDeclaration.maxLength() != null ) {

            if ( minMax == null ) {
                minMax = AnnotationSpec.builder(Size.class).addMember("max", "$L", typeDeclaration.maxLength());
            } else {

                minMax.addMember("max", "$L", typeDeclaration.maxLength());
            }
        }

        if ( minMax != null ) {
            typeSpec.addAnnotation(minMax.build());
        }
    }


    private void addFacetsForNumbers(FieldSpec.Builder typeSpec, NumberTypeDeclaration typeDeclaration) {

        FieldSpec t = typeSpec.build();
        if ( typeDeclaration.minimum() != null ) {
            if ( isInteger(t.type) ) {

                typeSpec.addAnnotation(AnnotationSpec.builder(Min.class).addMember("value", "$L", typeDeclaration.minimum().intValue()).build());
            }
        }

        if ( typeDeclaration.maximum() != null ) {
            if ( isInteger(t.type) ) {

                typeSpec.addAnnotation(AnnotationSpec.builder(Max.class).addMember("value", "$L", typeDeclaration.maximum().intValue()).build());
            }
        }
    }

    private boolean isInteger(TypeName type) {

        return type.box().toString().equals(Integer.class.getName())
                || type.box().toString().equals(Short.class.getName())
                || type.box().toString().equals(Byte.class.getName())
                || type.box().toString().equals(BigDecimal.class.getName())
                || type.box().toString().equals(Long.class.getName())
                || type.box().toString().equals(BigInteger.class.getName());
    }
}
