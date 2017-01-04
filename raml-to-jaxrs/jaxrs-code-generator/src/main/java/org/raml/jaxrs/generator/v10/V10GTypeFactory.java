package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.ScalarTypes;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 1/3/17.
 * Just potential zeroes and ones
 */
public class V10GTypeFactory {
    public static V10GType createScalar(V10TypeRegistry registry, TypeDeclaration typeDeclaration, Class<?> type) {
        return new V10GType(registry, typeDeclaration);
    }

    static V10GType createRequestBodyType(V10TypeRegistry registry, Resource resource, Method method,
            TypeDeclaration typeDeclaration) {

        return new V10GType(
                registry,
                typeDeclaration,
                Names.ramlTypeName(resource, method, typeDeclaration),
                Annotations.CLASS_NAME.get(typeDeclaration, Names.javaTypeName(resource, method, typeDeclaration)),
                true,
                getProperties(typeDeclaration, registry),
                getParents(typeDeclaration, registry));
    }

    static V10GType createResponseBodyType(V10TypeRegistry registry, Resource resource, Method method,
            Response response,
            TypeDeclaration typeDeclaration) {
        return new V10GType(
                registry,
                typeDeclaration,
                Names.ramlTypeName(resource, method, response, typeDeclaration),
                Annotations.CLASS_NAME.get(typeDeclaration, Names.javaTypeName(resource, method, response, typeDeclaration)),
                true,
                getProperties(typeDeclaration, registry),
                getParents(typeDeclaration, registry));
    }

    static V10GType createExplicitlyNamedType(V10TypeRegistry registry, String s, TypeDeclaration typeDeclaration) {
        return new V10GType(
                registry,
                typeDeclaration,
                s,
                Annotations.CLASS_NAME.get(typeDeclaration, Names.typeName(typeDeclaration.name())),
                false,
                getProperties(typeDeclaration, registry),
                getParents(typeDeclaration, registry));
    }

    static V10GType createPropertyType(V10TypeRegistry registry, TypeDeclaration typeDeclaration) {

        if ( ScalarTypes.isArray(typeDeclaration)) {

            return new V10GType(
                    registry,
                    typeDeclaration,
                    typeDeclaration.type(),
                    Annotations.CLASS_NAME.get(typeDeclaration, Names.typeName(typeDeclaration.name())),
                    false,
                    getProperties(typeDeclaration, registry),
                    getParents(typeDeclaration, registry));
        }

        if (ScalarTypes.scalarToJavaType(typeDeclaration) != null ) {

            return new V10GType(
                    registry,
                    typeDeclaration,
                    typeDeclaration.type(),
                    Annotations.CLASS_NAME.get(typeDeclaration, Names.typeName(typeDeclaration.name())),
                    false,
                    getProperties(typeDeclaration, registry),
                    getParents(typeDeclaration, registry));
        } else {
            return new V10GType(
                    registry,
                    typeDeclaration,
                    typeDeclaration.type(),
                    Annotations.CLASS_NAME.get(typeDeclaration, Names.typeName(typeDeclaration.name())),
                    TypeUtils.shouldCreateNewClass(typeDeclaration, typeDeclaration.parentTypes().toArray(new TypeDeclaration[0])),
                    getProperties(typeDeclaration, registry),
                    getParents(typeDeclaration, registry));
        }
    }

    static V10GType createExplicitlyNamedType(V10TypeRegistry registry, String ramlName, String javaClassName,
            TypeDeclaration typeDeclaration) {
        return new V10GType(
                registry,
                typeDeclaration,
                ramlName,
                Annotations.CLASS_NAME.get(typeDeclaration, javaClassName),
                false,
                getProperties(typeDeclaration, registry),
                getParents(typeDeclaration, registry));
    }

    private static List<V10GType> getParents(TypeDeclaration typeDeclaration, final V10TypeRegistry registry) {
        return Lists.transform(typeDeclaration.parentTypes(), new Function<TypeDeclaration, V10GType>() {
            @Nullable
            @Override
            public V10GType apply(@Nullable TypeDeclaration input) {
                return registry.fetchType(input);
            }
        });
    }

    private static List<V10GProperty> getProperties(final TypeDeclaration input, final V10TypeRegistry registry) {

        if ( input instanceof ObjectTypeDeclaration) {

            ObjectTypeDeclaration otd = (ObjectTypeDeclaration) input;
            return Lists.transform(otd.properties(), new Function<TypeDeclaration, V10GProperty>() {
                @Nullable
                @Override
                public V10GProperty apply(@Nullable TypeDeclaration declaration) {

                    return new V10GProperty(declaration, createPropertyType(registry, declaration));
                }
            });
        } else {

            return Collections.emptyList();
        }
    }
}
