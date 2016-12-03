package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Jean-Philippe Belanger on 11/30/16.
 * Just potential zeroes and ones
 */
public class JaxbTypeExtensionImpl implements TypeExtension {

    @Override
    public void onTypeImplementation(TypeSpec.Builder typeSpec) {

        typeSpec.addAnnotation(AnnotationSpec.builder(XmlRootElement.class).addMember("name", "$S", "booboo").build());
        typeSpec.addAnnotation(AnnotationSpec.builder(XmlAccessorType.class).addMember("value", "$T.$L", XmlAccessType.class, "FIELD").build());
    }
}
