package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.EnumerationGenerator;
import org.raml.jaxrs.generator.GObjectType;
import org.raml.jaxrs.generator.GProperty;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.GeneratorType;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.SchemaTypeFactory;
import org.raml.jaxrs.generator.V10TypeRegistry;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.types.CompositeRamlTypeGenerator;
import org.raml.jaxrs.generator.builders.types.PropertyInfo;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorImplementation;
import org.raml.jaxrs.generator.builders.types.RamlTypeGeneratorInterface;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/10/16.
 * Just potential zeroes and ones
 */
public class V10GType implements GType {

    private final V10TypeRegistry registry;
    private final TypeDeclaration typeDeclaration;
    private final String name;
    private final String defaultJavatypeName;
    private final boolean inline;
    private final List<GProperty> properties;
    private final List<GType> parentTypes;


    public static V10GType createRequestBodyType(V10TypeRegistry registry, Resource resource, Method method,
            TypeDeclaration typeDeclaration) {

        return new V10GType(
                registry,
                typeDeclaration,
                Names.ramlTypeName(resource, method, typeDeclaration),
                Names.javaTypeName(resource, method, typeDeclaration),
                true,
                getProperties(typeDeclaration, registry),
                getParents(typeDeclaration, registry));
    }

    private static List<GType> getParents(TypeDeclaration typeDeclaration, final V10TypeRegistry registry) {
        return Lists.transform(typeDeclaration.parentTypes(), new Function<TypeDeclaration, GType>() {
            @Nullable
            @Override
            public GType apply(@Nullable TypeDeclaration input) {
                return registry.fetchType(input);
            }
        });
    }

    public static V10GType createResponseBodyType(V10TypeRegistry registry, Resource resource, Method method,
            Response response,
            TypeDeclaration typeDeclaration) {
        return new V10GType(
                registry,
                typeDeclaration,
                Names.ramlTypeName(resource, method, response, typeDeclaration),
                Names.javaTypeName(resource, method, response, typeDeclaration),
                true,
                getProperties(typeDeclaration, registry),
                getParents(typeDeclaration, registry));
    }

    public static V10GType createExplicitlyNamedType(V10TypeRegistry registry, String s, TypeDeclaration typeDeclaration) {
        return new V10GType(
                registry,
                typeDeclaration,
                s,
                Names.typeName(typeDeclaration.name()),
                false,
                getProperties(typeDeclaration, registry),
                getParents(typeDeclaration, registry));
    }

    public static V10GType createExplicitlyNamedType(V10TypeRegistry registry, String ramlName, String javaClassName, TypeDeclaration typeDeclaration) {
        return new V10GType(
                registry,
                typeDeclaration,
                ramlName,
                javaClassName,
                false,
                getProperties(typeDeclaration, registry),
                getParents(typeDeclaration, registry));
    }


    private V10GType(V10TypeRegistry registry, TypeDeclaration typeDeclaration, String realName, String defaultJavatypeName, boolean inline, List<GProperty> properties, List<GType> parentTypes) {
        this.registry = registry;
        this.typeDeclaration = typeDeclaration;
        this.name = realName;
        this.defaultJavatypeName = defaultJavatypeName;
        this.inline = inline;
        this.properties = properties;
        this.parentTypes = parentTypes;
    }

    private static List<GProperty> getProperties(final TypeDeclaration input, final V10TypeRegistry registry) {

        if ( input instanceof ObjectTypeDeclaration) {

            ObjectTypeDeclaration otd = (ObjectTypeDeclaration) input;
            return Lists.transform(otd.properties(), new Function<TypeDeclaration, GProperty>() {
                @Nullable
                @Override
                public GProperty apply(@Nullable TypeDeclaration declaration) {

                    return new V10GProperty(declaration, createExplicitlyNamedType(registry, declaration.type(), declaration));
                }
            });
        } else {

            return Collections.emptyList();
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

    @Override
    public List<GType> parentTypes() {
        return parentTypes;
    }

    @Override
    public List<GProperty> properties() {

        return properties;
    }

    @Override
    public boolean declaresProperty(String name) {

        if ( typeDeclaration instanceof ObjectTypeDeclaration ) {

            for (TypeDeclaration typeDeclaration : ((ObjectTypeDeclaration) typeDeclaration).properties()) {
                if ( typeDeclaration.name().equals(name)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }


    @Override
    public boolean isArray() {
        return typeDeclaration instanceof ArrayTypeDeclaration;
    }

    @Override
    public GType arrayContents() {

        ArrayTypeDeclaration d = (ArrayTypeDeclaration) typeDeclaration;
        return createExplicitlyNamedType(registry, d.items().name().replaceAll("\\[\\]", ""),  d.items());
    }

    @Override
    public String defaultJavaTypeName() {

        return defaultJavatypeName;
    }

    @Override
    public boolean isEnum() {
        return  typeDeclaration instanceof StringTypeDeclaration && ((StringTypeDeclaration) typeDeclaration).enumValues().size() > 0;
    }

    @Override
    public List<String> enumValues() {
        return ((StringTypeDeclaration)typeDeclaration).enumValues();
    }

    @Override
    public boolean isInline() {
        return inline;
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

                createObjectType(currentBuild, V10GType.this, true);
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

                createEnumerationType(currentBuild, V10GType.this);
            }
        });
    }

    private TypeGenerator inlineTypeBuild(CurrentBuild currentBuild, GeneratorType type) {

        switch (type.getObjectType()) {

            case ENUMERATION_TYPE:
                return createEnumerationType(currentBuild, type.getDeclaredType());

            case PLAIN_OBJECT_TYPE:
                return createObjectType(currentBuild, (V10GType) type.getDeclaredType(),  false);

            case JSON_OBJECT_TYPE:
                return SchemaTypeFactory.createJsonType(currentBuild, type.getDeclaredType());

            case XML_OBJECT_TYPE:
                return SchemaTypeFactory.createXmlType(currentBuild, type.getDeclaredType());
        }

        throw new GenerationException("don't know what to do with type " + type.getDeclaredType());
    }

    public TypeGenerator createObjectType(CurrentBuild currentBuild, V10GType originalType, boolean publicType) {

        List<GType> parentTypes = originalType.parentTypes();
        Map<String, JavaPoetTypeGenerator> internalTypes = new HashMap<>();
        int internalTypeCounter = 0;
        List<PropertyInfo> properties = new ArrayList<>();
        for (GProperty declaration : originalType.properties()) {

            if (declaration.isInternal()) {
                String internalTypeName = Integer.toString(internalTypeCounter);

                GType type = registry.createInlineType(internalTypeName, Names.typeName(declaration.name(), "Type"),
                        (TypeDeclaration) declaration.implementation());
                TypeGenerator internalGenerator = inlineTypeBuild(currentBuild, GeneratorType.generatorFrom(type));
                if ( internalGenerator instanceof JavaPoetTypeGenerator ) {
                    internalTypes.put(internalTypeName, (JavaPoetTypeGenerator) internalGenerator);
                    properties.add(new PropertyInfo(declaration.overrideType(type)));
                    internalTypeCounter ++;
                } else {
                    throw new GenerationException("internal type bad");
                }
            } else {
                properties.add(new PropertyInfo(declaration));
            }

        }

        if ( currentBuild.implementationsOnly() ) {

            ClassName impl = buildClassName(currentBuild.getModelPackage(), originalType.defaultJavaTypeName(), publicType);

            RamlTypeGeneratorImplementation implg = new RamlTypeGeneratorImplementation(currentBuild, impl, null, properties, internalTypes, originalType);

            if ( publicType ) {
                currentBuild.newGenerator(originalType.name(), implg);
            }
            return implg;
        } else {

            ClassName interf = buildClassName(currentBuild.getModelPackage(), originalType.defaultJavaTypeName(), publicType);
            ClassName impl = buildClassName(currentBuild.getModelPackage(), originalType.defaultJavaTypeName() + "Impl", publicType);

            RamlTypeGeneratorImplementation implg = new RamlTypeGeneratorImplementation(currentBuild, impl, interf,
                    properties, internalTypes, originalType);
            RamlTypeGeneratorInterface intg = new RamlTypeGeneratorInterface(currentBuild, interf, parentTypes, properties, internalTypes, originalType);
            CompositeRamlTypeGenerator gen = new CompositeRamlTypeGenerator(intg, implg);

            if ( publicType ) {
                currentBuild.newGenerator(originalType.name(), gen);
            }
            return gen;
        }
    }

    private TypeGenerator createEnumerationType(CurrentBuild currentBuild, GType type) {
        JavaPoetTypeGenerator generator =  new EnumerationGenerator(
                currentBuild,
                ((V10GType)type).implementation(),
                ClassName.get(currentBuild.getModelPackage(), type.defaultJavaTypeName()),
                type.enumValues());

        currentBuild.newGenerator(type.name(), generator);
        return generator;
    }

    private ClassName buildClassName(String pack, String name, boolean publicType) {

        if ( publicType ) {
            return ClassName.get(pack, name);
        } else {

            return ClassName.get("", name);
        }
    }


}
