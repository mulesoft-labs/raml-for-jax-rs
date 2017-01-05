package org.raml.jaxrs.generator.v10;

import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/4/17.
 * Just potential zeroes and ones
 */
public class V10GTypeScalar implements V10GType {

    private final TypeDeclaration scalar;
    private final String ramlName;
    private final TypeName javaName;

    public V10GTypeScalar(TypeDeclaration scalar) {

        this.scalar = scalar;
        this.ramlName = scalar.name();
        this.javaName = classToTypeName(ScalarTypes.scalarToJavaType(scalar));
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
    public boolean isJson() {
        return false;
    }

    @Override
    public boolean isUnion() {
        return false;
    }

    @Override
    public boolean isXml() {
        return false;
    }

    @Override
    public boolean isObject() {
        return false;
    }

    @Override
    public String schema() {
        return null;
    }

    @Override
    public List<V10GType> parentTypes() {
        return Collections.emptyList();
    }

    @Override
    public List<V10GProperty> properties() {
        return Collections.emptyList();
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public GType arrayContents() {
        return null;
    }

    @Override
    public TypeName defaultJavaTypeName(String pack) {
        return javaName;
    }

    @Override
    public ClassName javaImplementationName(String pack) {
        return ClassName.get(ScalarTypes.scalarToJavaType(scalar));
    }

    @Override
    public boolean isEnum() {
        return false;
    }

    @Override
    public List<String> enumValues() {
        return Collections.emptyList();
    }

    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public Collection<V10GType> childClasses(String typeName) {
        return Collections.emptyList();
    }

    @Override
    public void construct(CurrentBuild currentBuild, GObjectType objectType) {

    }

    private TypeName classToTypeName(Class scalar) {
        if ( scalar.isPrimitive()) {
            switch(scalar.getSimpleName()) {
                case "int":
                    return TypeName.INT;

                case "boolean":
                    return TypeName.BOOLEAN;

                case "double":
                    return TypeName.DOUBLE;

                case "float":
                    return TypeName.FLOAT;

                case "byte":
                    return TypeName.BYTE;

                case "char":
                    return TypeName.CHAR;

                case "short":
                    return TypeName.SHORT;

                case "long":
                    return TypeName.LONG;

                case "void":
                    return TypeName.VOID; // ?

                default:
                    throw new GenerationException("can't handle type: " + scalar);
            }
        } else {
            return ClassName.get(scalar);
        }
    }
}
