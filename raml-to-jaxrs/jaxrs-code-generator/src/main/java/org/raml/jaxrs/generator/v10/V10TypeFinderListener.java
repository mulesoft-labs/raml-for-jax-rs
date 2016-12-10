package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.GeneratorType;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.TypeFinderListener;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import java.util.Map;

import static org.raml.jaxrs.generator.v10.V10ObjectType.JSON_OBJECT_TYPE;
import static org.raml.jaxrs.generator.v10.V10ObjectType.PLAIN_OBJECT_TYPE;
import static org.raml.jaxrs.generator.v10.V10ObjectType.XML_OBJECT_TYPE;

/**
 * Created by Jean-Philippe Belanger on 12/8/16.
 * Just potential zeroes and ones
 */
public class V10TypeFinderListener implements TypeFinderListener<V10GeneratorContext> {
    private final Api api;
    private final Map<String, GeneratorType<V10GeneratorContext>> foundTypes;

    public V10TypeFinderListener(Api api, Map<String, GeneratorType<V10GeneratorContext>> foundTypes) {
        this.api = api;

        this.foundTypes = foundTypes;
    }

    @Override
    public void newType(V10GeneratorContext context) {

        TypeDeclaration typeDeclaration = context.getTypeDeclaration();
        if ( context.getResponse() != null ) {

            newResponseType(context);
            return;
        }

        if ( context.getMethod() != null ) {

            newRequestType(context);
            return;
        }

        // Just a plain type we found.
        if (typeDeclaration instanceof ObjectTypeDeclaration) {
            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(PLAIN_OBJECT_TYPE,
                    typeDeclaration.name(), Names.typeName(typeDeclaration.name()),
                    new V10GeneratorContext(api, typeDeclaration));
            foundTypes.put(typeDeclaration.name(), generator);
            return;
        }

        if (typeDeclaration instanceof XMLTypeDeclaration) {

            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(XML_OBJECT_TYPE,
                    typeDeclaration.name(), Names.typeName(typeDeclaration.name()),
                    new V10GeneratorContext(api, typeDeclaration));
            foundTypes.put(typeDeclaration.name(), generator);
            return;
        }

        if (typeDeclaration instanceof JSONTypeDeclaration) {

            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(JSON_OBJECT_TYPE,
                    typeDeclaration.name(), Names.typeName(typeDeclaration.name()),
                    new V10GeneratorContext(api, typeDeclaration));
            foundTypes.put(typeDeclaration.name(), generator);
            return;
        }
    }

    private void newResponseType(V10GeneratorContext context) {


        TypeDeclaration typeDeclaration = context.getTypeDeclaration();
        GeneratorType<V10GeneratorContext> supertype = foundTypes.get(typeDeclaration.type());
        if (supertype == null || ! TypeUtils.shouldCreateNewClass(typeDeclaration, supertype.getContext().getTypeDeclaration())) {
            return;
        }

        Response response = context.getResponse();
        Method method = context.getMethod();
        Resource resource = context.getResource();

        // Just a plain type we found.
        String ramlType = Names.ramlTypeName(resource, method, response, typeDeclaration);
        String javaType = Names.javaTypeName(resource, method, response, typeDeclaration);

        if (typeDeclaration instanceof ObjectTypeDeclaration) {

            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(PLAIN_OBJECT_TYPE, ramlType, javaType,
                    new V10GeneratorContext(api, resource, method, response, typeDeclaration));
            foundTypes.put(ramlType, generator);

            return;
        }

        if (typeDeclaration instanceof XMLTypeDeclaration) {

            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(XML_OBJECT_TYPE, ramlType, javaType,
                    new V10GeneratorContext(api, resource, method, response, typeDeclaration));
            foundTypes.put(ramlType, generator);
            return;
        }

        if (typeDeclaration instanceof JSONTypeDeclaration) {

            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(JSON_OBJECT_TYPE, ramlType, javaType,
                    new V10GeneratorContext(api, resource, method, response, typeDeclaration));
            foundTypes.put(ramlType, generator);
            return;
        }

    }

    private void newRequestType(V10GeneratorContext context) {

        TypeDeclaration typeDeclaration = context.getTypeDeclaration();
        GeneratorType<V10GeneratorContext> supertype = foundTypes.get(typeDeclaration.type());
        if (supertype == null || ! TypeUtils.shouldCreateNewClass(typeDeclaration, supertype.getContext().getTypeDeclaration())) {
            return;
        }

        Method method = context.getMethod();
        Resource resource = context.getResource();

        String ramlType = Names.ramlTypeName(resource, method, typeDeclaration);
        String javaType = Names.javaTypeName(resource, method, typeDeclaration);
        if (typeDeclaration instanceof ObjectTypeDeclaration) {

            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(PLAIN_OBJECT_TYPE, ramlType, javaType,
                    new V10GeneratorContext(api, resource, method, typeDeclaration));
            foundTypes.put(ramlType, generator);
            return;
        }

        if (typeDeclaration instanceof XMLTypeDeclaration) {

            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(XML_OBJECT_TYPE, ramlType, javaType,
                    new V10GeneratorContext(api, resource, method, typeDeclaration));
            foundTypes.put(ramlType, generator);
            return;
        }

        if (typeDeclaration instanceof JSONTypeDeclaration) {

            GeneratorType<V10GeneratorContext> generator = new GeneratorType<>(JSON_OBJECT_TYPE, ramlType, javaType,
                    new V10GeneratorContext(api, resource, method, typeDeclaration));
            foundTypes.put(ramlType, generator);
            return;
        }
    }
}
