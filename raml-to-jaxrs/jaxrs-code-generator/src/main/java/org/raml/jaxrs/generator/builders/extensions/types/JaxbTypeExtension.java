package org.raml.jaxrs.generator.builders.extensions.types;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jean-Philippe Belanger on 11/30/16.
 * Just potential zeroes and ones
 */
public class JaxbTypeExtension extends TypeExtensionHelper {

    @Override
    public void onTypeImplementation(TypeSpec.Builder typeSpec, TypeDeclaration typeDeclaration) {

        typeSpec.addAnnotation(AnnotationSpec.builder(XmlRootElement.class).addMember("name", "$S", typeDeclaration.name()).build());
        typeSpec.addAnnotation(AnnotationSpec.builder(XmlAccessorType.class).addMember("value", "$T.$L", XmlAccessType.class, "FIELD").build());
    }

    @Override
    public void onFieldImplementation(FieldSpec.Builder fieldSpec, TypeDeclaration typeDeclaration) {

        fieldSpec.addAnnotation(AnnotationSpec.builder(XmlElement.class).addMember("name", "$S", typeDeclaration.name()).build());
    }

    @Override
    public void onGetterMethodImplementation(MethodSpec.Builder typeSpec,
            TypeDeclaration typeDeclaration) {

    }

    @Override
    public void onEnumConstant(TypeSpec.Builder builder, TypeDeclaration typeDeclaration, String name) {

        builder.addAnnotation(AnnotationSpec.builder(XmlEnumValue.class).addMember("value", "$S", name).build());
    }

    @Override
    public void onEnumerationClass(TypeSpec.Builder builder, TypeDeclaration typeDeclaration) {

        builder.addAnnotation(AnnotationSpec.builder(XmlEnum.class).build());
    }
}
