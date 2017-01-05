package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GProperty;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;

import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class PropertyInfo {

    private final V10TypeRegistry registry;
    private final GProperty property;

    public PropertyInfo(V10TypeRegistry registry, GProperty property) {
        this.registry = registry;
        this.property = property;
    }

    public String getName() {
        return property.name();
    }


    public GType getType() {
        return property.type();
    }


    public TypeName resolve(CurrentBuild currentBuild) {

        return property.type().defaultJavaTypeName(currentBuild.getModelPackage());
    }
}
