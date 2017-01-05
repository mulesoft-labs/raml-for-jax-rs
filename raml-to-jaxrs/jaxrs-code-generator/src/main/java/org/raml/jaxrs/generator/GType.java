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
    TypeName defaultJavaTypeName(String pack);

    boolean isJson();
    boolean isXml();
    boolean isObject();
    boolean isArray();
    boolean isEnum();
    boolean isUnion();
    List<String> enumValues();
    String schema();
    GType arrayContents();

/*
    boolean isInline();
    List<GType> parentTypes();
*/


/*
    List<GProperty> properties();


*/

    void construct(CurrentBuild currentBuild, GObjectType objectType);

}
