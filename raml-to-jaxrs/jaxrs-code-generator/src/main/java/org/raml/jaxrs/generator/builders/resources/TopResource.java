package org.raml.jaxrs.generator.builders.resources;

import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GMethod;
import org.raml.jaxrs.generator.GParameter;
import org.raml.jaxrs.generator.GResource;
import org.raml.jaxrs.generator.GResponse;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.HTTPMethods;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.v10.ResourceUtils;
import org.raml.jaxrs.generator.v10.TypeUtils;

import javax.annotation.Nullable;
import javax.lang.model.element.Modifier;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Jean-Philippe Belanger on 10/27/16.
 * Abstraction of creation.
 */
public class TopResource implements ResourceGenerator {

    private final CurrentBuild build;
    private final GResource topResource;
    private final String name;
    private final String uri;


    public TopResource(CurrentBuild build, GResource resource, String name, String uri) {

        this.build = build;
        this.topResource = resource;
        this.name = name;
        this.uri = uri;
    }

    @Override
    public void output(CodeContainer<TypeSpec> container) throws IOException {


        final TypeSpec.Builder typeSpec = TypeSpec.interfaceBuilder(Names.typeName(name))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Path.class)
                        .addMember("value", "$S", uri).build());

        buildResource(typeSpec, topResource);

        recurse(typeSpec, topResource);
        container.into(typeSpec.build());
    }

    private void recurse(TypeSpec.Builder typeSpec, GResource parentResource) {

        for (GResource resource : parentResource.resources()) {

            buildResource(typeSpec, resource);
            recurse(typeSpec, resource);
        }
    }

    private void buildResource(TypeSpec.Builder typeSpec, GResource currentResource) {
/*
        Multimap<GMethod, GType> incomingBodies = ArrayListMultimap.create();
        Multimap<GMethod, GResponse> responses = ArrayListMultimap.create();
        ResourceUtils.fillInBodiesAndResponses(currentResource, incomingBodies, responses);

        createResponseClass(typeSpec, incomingBodies, responses);

        for (GMethod gMethod : incomingBodies.keySet()) {

            Set<String> mediaTypesForMethod = fetchAllMediaTypesForMethod(gMethod);
            TreeSet<GType> decls = new TreeSet<>(new TypeDeclarationTypeComparator());

            Multimap<String, String> ramlTypeToMediaType = ArrayListMultimap.create();
            for (GType typeDeclaration : incomingBodies.get(gMethod)) {
                if ( typeDeclaration != null ) {
                    decls.add(typeDeclaration);
                    ramlTypeToMediaType.put(typeDeclaration.type(), typeDeclaration.name());
                }
            }

            String methodName = Names.resourceMethodName(gMethod.resource(), gMethod);
            if (decls.size() == 0) {
                MethodSpec.Builder methodSpec = createMethodBuilder(gMethod, methodName, mediaTypesForMethod);
                typeSpec.addMethod(methodSpec.build());
            } else {
                for (GType typeDeclaration : decls) {

                    MethodSpec.Builder methodSpec = createMethodBuilder(gMethod, methodName, mediaTypesForMethod);
                    TypeName name = build.getJavaType(typeDeclaration, gMethod.resource(), gMethod);
                    methodSpec.addParameter(ParameterSpec.builder(name, "entity").build());
                    handleMethodConsumer(methodSpec, ramlTypeToMediaType, typeDeclaration);
                    typeSpec.addMethod(methodSpec.build());
                }
            }
        }
*/
    }

    private Set<String> fetchAllMediaTypesForMethod(GMethod gMethod) {

        Set<String> mediaTypes = new HashSet<>();
        for (GResponse gResponse : gMethod.responses()) {

            mediaTypes.addAll(Lists.transform(gResponse.body(), new Function<GType, String>() {
                @Nullable
                @Override
                public String apply(@Nullable GType input) {
                    return input.name();
                }
            }));
        }

        return mediaTypes;
    }

    private void createResponseClass(TypeSpec.Builder typeSpec, Multimap<GMethod, GType> bodies, Multimap<GMethod, GResponse> responses) {

/*
        Set<GMethod> allMethods = new HashSet<>();
        allMethods.addAll(bodies.keySet());
        allMethods.addAll(responses.keySet());
        for (GMethod gMethod : allMethods) {

            TypeSpec.Builder responseClass = TypeSpec
                    .classBuilder(Names.responseClassName(gMethod.resource(), gMethod))
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .superclass(ClassName.get(build.getResourcePackage(), "ResponseDelegate"))
                    .addMethod(
                            MethodSpec.constructorBuilder()
                                    .addParameter(javax.ws.rs.core.Response.class, "GResponse")
                                    .addModifiers(Modifier.PRIVATE)
                                    .addCode("super(GResponse);\n").build()
                    );


            TypeSpec currentClass = responseClass.build();
            for (GResponse gResponse : responses.get(gMethod)) {

                if ( gResponse == null ) {
                    continue;
                }
                if(gResponse.body().size() == 0 ) {
                    String httpCode = gResponse.code();
                    MethodSpec.Builder builder = MethodSpec.methodBuilder("respond" + httpCode);
                    builder
                            .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                            .addStatement("GResponse.ResponseBuilder responseBuilder = GResponse.status(" + httpCode + ")")
                            .addStatement("return new $N(responseBuilder.build())", currentClass)
                            .returns(TypeVariableName.get(currentClass.name))
                            .build();

                    responseClass.addMethod(builder.build());
                } else {
                    for (GType typeDeclaration : gResponse.body()) {

                        String httpCode = gResponse.code();
                        MethodSpec.Builder builder = MethodSpec.methodBuilder( Names.methodName("respond", httpCode,  "With", typeDeclaration.name() ) );
                        builder
                                .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                                .addStatement("GResponse.ResponseBuilder responseBuilder = GResponse.status(" + httpCode + ")")
                                .addStatement("responseBuilder.entity(entity)")
                                .addStatement("return new $N(responseBuilder.build())", currentClass)
                                .returns(TypeVariableName.get(currentClass.name))
                                .build();
                        TypeName typeName = build.getJavaType(typeDeclaration, gMethod.resource(), gMethod, gResponse);
                        if (typeName == null) {
                            throw new GenerationException(typeDeclaration + " was not seen before");
                        }

                        builder.addParameter(ParameterSpec.builder(typeName, "entity").build());
                        responseClass.addMethod(builder.build());
                    }
                }
            }

            typeSpec.addType(responseClass.build());
        }
*/
    }
/*
    private MethodSpec.Builder createMethodBuilder(GMethod gMethod, String methodName, Set<String> mediaTypesForMethod) {

        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC);

        for (GParameter typeDeclaration : GMethod.resource().uriParameters()) {

            if (TypeUtils.isComposite(typeDeclaration)) {
                throw new GenerationException("uri parameter is composite: " + typeDeclaration);
            }

            methodSpec.addParameter(
                    ParameterSpec.builder(
                            build.getJavaType(typeDeclaration), Names.methodName(typeDeclaration.name()))
                            .addAnnotation(
                                    AnnotationSpec.builder(PathParam.class).addMember("value", "$S", typeDeclaration.name())
                                            .build())
                            .build());

        }
        for (GParameter typeDeclaration : gMethod.queryParameters()) {
            if (TypeUtils.isComposite(typeDeclaration)) {
                throw new GenerationException("query parameter is composite: " + typeDeclaration);
            }

            methodSpec.addParameter(
                    ParameterSpec.builder(
                            build.getJavaType(typeDeclaration), Names.methodName(typeDeclaration.name()))
                            .addAnnotation(
                                    AnnotationSpec.builder(QueryParam.class).addMember("value", "$S", typeDeclaration.name())
                                            .build())
                            .build());
        }

        methodSpec
                .addAnnotation(AnnotationSpec.builder(HTTPMethods.methodNameToAnnotation(gMethod.method())).build());

        if ( gMethod.resource().parentResource() != null ) {

            methodSpec.addAnnotation(AnnotationSpec.builder(Path.class).addMember("value", "$S", GMethod.resource().relativeUri().value()).build());
        }

        methodSpec.returns(ClassName.get("", Names.responseClassName(gMethod.resource(), gMethod)));

        if ( mediaTypesForMethod.size() > 0 ) {
            AnnotationSpec.Builder ann = buildAnnotation(mediaTypesForMethod, Produces.class);
            methodSpec.addAnnotation(ann.build());
        }
        return methodSpec;
    }

    private void handleMethodConsumer(MethodSpec.Builder methodSpec,
            Multimap<String, String> ramlTypeToMediaType,
            GType typeDeclaration) {
        Collection<String> mediaTypes = ramlTypeToMediaType.get(typeDeclaration.type());

        AnnotationSpec.Builder ann = buildAnnotation(mediaTypes, Consumes.class);
        methodSpec.addAnnotation(ann.build());
    }

    private AnnotationSpec.Builder buildAnnotation(Collection<String> mediaTypes, Class<? extends Annotation> type) {
        AnnotationSpec.Builder ann = AnnotationSpec.builder(type);
        for (String mediaType : mediaTypes) {

            ann.addMember("value", "$S", mediaType);
        }
        return ann;
    }
*/

    private static class TypeDeclarationTypeComparator implements Comparator<GType> {
        @Override
        public int compare(GType o1, GType o2) {
            return o1.type().compareTo(o2.type());
        }
    }
}
