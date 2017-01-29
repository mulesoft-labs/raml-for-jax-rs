package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.v10.Annotations;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

/**
 * Created by Jean-Philippe Belanger on 1/4/17.
 * Just potential zeroes and ones
 */
public class V10GTypeScalar extends V10GTypeHelper {

    private final TypeDeclaration scalar;
    private final String ramlName;


    public V10GTypeScalar(String name, TypeDeclaration scalar) {
        super(name);
        this.scalar = scalar;
        this.ramlName = name;
    }

    @Override
    public TypeDeclaration implementation() {
        return scalar;
    }

    @Override
    public String type() {
        return ramlName;
    }

    @Override
    public String name() {
        return ramlName;
    }

    @Override
    public TypeName defaultJavaTypeName(String pack) {

        String annotation = Annotations.CLASS_NAME.get((String)null, scalar);
        if(annotation == null ) {

            return ScalarTypes.classToTypeName(ScalarTypes.scalarToJavaType(scalar));
        } else {

            if ( annotation.contains(".")) {
                return ClassName.bestGuess(annotation);
            } else {
                return ClassName.get(pack, annotation);
            }
        }
    }

    @Override
    public ClassName javaImplementationName(String pack) {
        return ClassName.get(ScalarTypes.scalarToJavaType(scalar));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if ( ! (o instanceof V10GType) ) {

            return false;
        }

        V10GType v10GType = (V10GType) o;

        return ramlName.equals(v10GType.name());
    }

    @Override
    public int hashCode() {
        return ramlName.hashCode();
    }

}
