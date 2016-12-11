package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GProperty;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class PropertyInfo {

    private final GProperty property;
    private String internalTypeName;
    private  GType type;

    public PropertyInfo(GProperty property) {
        this.property = property;
    }

    public PropertyInfo(String name, String internalTypeName, GType declaration) {
        this.property  = null;
        this.internalTypeName = internalTypeName;
        this.type = declaration;
    }

    public String getName() {
        return property.name();
    }


    public GType getType() {
        return property.type();
    }


    public TypeName resolve(CurrentBuild currentBuild, Map<String, JavaPoetTypeGenerator> internalTypes) {

        // If the type is not internal, then it must exist.  The type of the property must be correct.
        if ( internalTypeName == null ) {

            return currentBuild.getJavaType(property.type(), internalTypes);
        } else {

            TypeGenerator tg = internalTypes.get(internalTypeName);
            if (  tg == null ) {
                throw new GenerationException("unable to resolve type for " + property);
            } else {

                return tg.getGeneratedJavaType();
            }
        }
    }
}
