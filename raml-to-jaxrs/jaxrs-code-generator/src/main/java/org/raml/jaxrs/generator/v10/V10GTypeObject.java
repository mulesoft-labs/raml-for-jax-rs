package org.raml.jaxrs.generator.v10;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/3/17.
 * Just potential zeroes and ones
 */
public class V10GTypeObject {

    private final V10TypeRegistry registry;
    private final TypeDeclaration typeDeclaration;
    private final String name;
    private final String defaultJavatypeName;
    private final boolean inline;
    private final List<V10GProperty> properties;
    private final List<V10GType> parentTypes;


    V10GType(V10TypeRegistry registry, TypeDeclaration typeDeclaration, String realName, String defaultJavatypeName,
            boolean inline, List<V10GProperty> properties, List<V10GType> parentTypes) {
        this.registry = registry;
        this.typeDeclaration = typeDeclaration;
        this.name = realName;
        this.defaultJavatypeName = defaultJavatypeName;
        this.inline = inline;
        this.properties = properties;
        this.parentTypes = parentTypes;

        if (! isInline() && isObject() && !name.equals("object") ) {

            registry.addChildToParent(parentTypes(), this);
        }
    }

    @Override
    public TypeDeclaration implementation() {
        return typeDeclaration;
    }

    @Override
    public String type() {
        return typeDeclaration.type();
    }

    @Override
    public String name() {

        return name;
    }

    @Override
    public boolean isJson() {

        return typeDeclaration instanceof JSONTypeDeclaration;
    }

    @Override
    public boolean isUnion() {

        return typeDeclaration instanceof UnionTypeDeclaration;
    }

    @Override
    public boolean isXml() {
        return typeDeclaration instanceof XMLTypeDeclaration;
    }

    @Override
    public boolean isObject() {
        return typeDeclaration instanceof ObjectTypeDeclaration;
    }

    @Override
    public String schema() {
        if ( typeDeclaration instanceof XMLTypeDeclaration ) {
            return ((XMLTypeDeclaration) typeDeclaration).schemaContent();
        }

        if ( typeDeclaration instanceof JSONTypeDeclaration) {

            return ((JSONTypeDeclaration) typeDeclaration).schemaContent();
        }

        throw new GenerationException("type " + this + " has no schema");
    }

    public List<V10GType> parentTypes() {
        return parentTypes;
    }

    public List<V10GProperty> properties() {

        return properties;
    }


    @Override
    public boolean isArray() {
        return typeDeclaration instanceof ArrayTypeDeclaration;
    }

    @Override
    public GType arrayContents() {

        ArrayTypeDeclaration d = (ArrayTypeDeclaration) typeDeclaration;
        return V10GTypeFactory.createExplicitlyNamedType(registry, d.items().name().replaceAll("\\[\\]", ""),  d.items());
    }

    @Override
    public TypeName defaultJavaTypeName(String pack) {

     /*   if ( isArray() ) {

            GType items = arrayContents();

            return ParameterizedTypeName.get(ClassName.get(List.class), items.defaultJavaTypeName(pack));
        }
*/
        if ( isInline() ) {
            return ClassName.get("", defaultJavatypeName);
        } else {
            return ClassName.get(pack, defaultJavatypeName);
        }
    }

    public ClassName javaImplementationName(String pack) {

        if ( isInline() ) {

            return ClassName
                    .get("", Annotations.IMPLEMENTATION_CLASS_NAME.get(typeDeclaration, defaultJavatypeName + "Impl"));
        } else {
            return ClassName
                    .get(pack, Annotations.IMPLEMENTATION_CLASS_NAME.get(typeDeclaration, defaultJavatypeName + "Impl"));
        }
    }

    @Override
    public boolean isEnum() {
        return  typeDeclaration instanceof StringTypeDeclaration
                && ((StringTypeDeclaration) typeDeclaration).enumValues().size() > 0;
    }

    @Override
    public List<String> enumValues() {
        return ((StringTypeDeclaration)typeDeclaration).enumValues();
    }

    public boolean isInline() {
        return TypeUtils.shouldCreateNewClass(typeDeclaration, typeDeclaration.parentTypes().toArray(new TypeDeclaration[0]));
    }


    public Collection<V10GType> childClasses(String typeName) {

        return new HashSet<>(registry.getChildClasses().get(typeName));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        V10GType v10GType = (V10GType) o;

        return name.equals(v10GType.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "V10GType{" +
                "input=" + typeDeclaration.name() + ":" + typeDeclaration.type()+
                ", name='" + name() + '\'' +
                '}';
    }

    @Override
    public void construct(final CurrentBuild currentBuild, GObjectType objectType) {
        objectType.dispatch(new GObjectType.GObjectTypeDispatcher() {

            @Override
            public void onPlainObject() {

                V10TypeFactory.createObjectType(registry, currentBuild, V10GType.this, true);
            }

            @Override
            public void onXmlObject() {

                SchemaTypeFactory.createXmlType(currentBuild, V10GType.this);
            }

            @Override
            public void onJsonObject() {

                SchemaTypeFactory.createJsonType(currentBuild, V10GType.this);
            }

            @Override
            public void onEnumeration() {

                V10TypeFactory.createEnumerationType(currentBuild, V10GType.this);
            }

            @Override
            public void onUnion() {
                V10TypeFactory.createUnion(currentBuild, V10GType.this);
            }
        });
    }


}
