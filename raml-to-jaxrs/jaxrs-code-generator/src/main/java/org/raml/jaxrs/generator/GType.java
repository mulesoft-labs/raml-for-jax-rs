package org.raml.jaxrs.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public interface GType extends GAbstraction {

    String type();
    String name();

    boolean isJson();
    boolean isXml();

    String schema();

    boolean isArray();

    List<GType> parentTypes();

    boolean declaresProperty(String name);

    boolean isObject();

    List<GProperty> properties();

    GType arrayContents();
}
