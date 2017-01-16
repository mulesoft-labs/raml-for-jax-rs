package org.raml.jaxrs.generator.v10;

import org.raml.jaxrs.generator.GenerationException;
import org.raml.jaxrs.generator.extension.ResourceExtension;
import org.raml.jaxrs.generator.extension.ResourceMethodExtension;
import org.raml.jaxrs.generator.extension.ResponseClassExtension;
import org.raml.jaxrs.generator.extension.ResponseMethodExtension;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;

/**
 * Created by Jean-Philippe Belanger on 1/2/17.
 * Just potential zeroes and ones
 */
public abstract class Annotations<T> {


    public static Annotations<String> CLASS_NAME = new Annotations<String>() {
        @Override
        public String get(Annotable target) {

            return getWithDefault(target, "types", "classname", null);
        }
    };

    public static Annotations<String> IMPLEMENTATION_CLASS_NAME = new Annotations<String>() {
        @Override
        public String get(Annotable target) {

            return getWithDefault(target, "types", "implementationClassName", null);
        }
    };

    public static Annotations<Boolean> USE_PRIMITIVE_TYPE = new Annotations<Boolean>() {
        @Override
        public Boolean get(Annotable target) {

            return getWithDefault(target, "types", "usePrimitiveType", false);
        }

    };

    public static Annotations<Boolean> ABSTRACT = new Annotations<Boolean>() {
        @Override
        public Boolean get(Annotable target) {

            return getWithDefault(target, "types", "abstract", false);
        }
    };

    public static Annotations<? extends ResourceExtension> ON_RESOURCE_CLASS_CREATION = new Annotations<ResourceExtension>() {
        @Override
        public ResourceExtension get(Annotable target) {
            String className = getWithDefault(target, "resources", "onResourceCreation", null);
            return createExtension(className, ResourceExtension.NULL_EXTENSION);
        }
    };

    public static Annotations<? extends ResourceExtension> ON_RESOURCE_CLASS_FINISH = new Annotations<ResourceExtension>() {
        @Override
        public ResourceExtension get(Annotable target) {
            String className = getWithDefault(target, "resources", "onResourceFinish", null);
            return createExtension(className, ResourceExtension.NULL_EXTENSION);
        }
    };

    public static Annotations<? extends ResourceMethodExtension> ON_METHOD_CREATION = new Annotations<ResourceMethodExtension>() {
        @Override
        public ResourceMethodExtension get(Annotable target) {
            String className = getWithDefault(target, "methods", "onMethodCreation", null);
            return createExtension(className, ResourceMethodExtension.NULL_EXTENSION);
        }
    };

    public static Annotations<? extends ResourceMethodExtension> ON_METHOD_FINISH = new Annotations<ResourceMethodExtension>() {
        @Override
        public ResourceMethodExtension get(Annotable target) {
            String className = getWithDefault(target, "methods", "onMethodFinish", null);
            return createExtension(className, ResourceMethodExtension.NULL_EXTENSION);
        }
    };

    public static Annotations<? extends ResponseClassExtension> ON_RESPONSE_CLASS_CREATION = new Annotations<ResponseClassExtension>() {
        @Override
        public ResponseClassExtension get(Annotable target) {
            String className = getWithDefault(target, "methods", "onResponseClassCreation", null);
            return createExtension(className, ResponseClassExtension.NULL_EXTENSION);
        }
    };

    public static Annotations<? extends ResponseClassExtension> ON_RESPONSE_CLASS_FINISH = new Annotations<ResponseClassExtension>() {
        @Override
        public ResponseClassExtension get(Annotable target) {
            String className = getWithDefault(target, "methods", "onResponseClassFinish", null);
            return createExtension(className, ResponseClassExtension.NULL_EXTENSION);
        }
    };

    public static Annotations<? extends ResponseMethodExtension> ON_RESPONSE_METHOD_CREATION = new Annotations<ResponseMethodExtension>() {
        @Override
        public ResponseMethodExtension get(Annotable target) {
            String className = getWithDefault(target, "responses", "onResponseMethodCreation", null);
            return createExtension(className, ResponseMethodExtension.NULL_EXTENSION);
        }
    };

    public static Annotations<? extends ResponseMethodExtension> ON_RESPONSE_METHOD_FINISH = new Annotations<ResponseMethodExtension>() {
        @Override
        public ResponseMethodExtension get(Annotable target) {
            String className = getWithDefault(target, "responses", "onResponseMethodFinish", null);
            return createExtension(className, ResponseMethodExtension.NULL_EXTENSION);
        }
    };

    private static <T> T createExtension(String className, T nullExtension) {
        if ( className == null ) {

            return nullExtension;
        } else {

            try {
                return (T) Class.forName(className).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new GenerationException("Cannot find resource creation extension");
            }
        }
    }

    private static<T> T getWithDefault(Annotable target, String annotationName, String propName, T def) {
        T b = Annotations.evaluate(target, annotationName, propName);
        if ( b == null ) {

            return def;
        } else {
            return b;
        }
    }


    private static<T> T evaluate(Annotable target, String annotationName, String parameterName) {
        AnnotationRef annotationRef = Annotations.findRef(target, annotationName);
        if (annotationRef == null) {
            return null;
        }

        Object o = findProperty(annotationRef, parameterName);
        if ( o != null ) {
            return (T) o;
        }

        return null;
    }

    private static Object findProperty(AnnotationRef annotationRef, String propName) {


        for (TypeInstanceProperty typeInstanceProperty : annotationRef.structuredValue().properties()) {
            if (typeInstanceProperty.name().equalsIgnoreCase(propName)) {
                return typeInstanceProperty.value().value();
            }
        }

        return null;
    }

    private static AnnotationRef findRef(Annotable annotable, String annotation) {

        for (AnnotationRef annotationRef : annotable.annotations()) {
            if (annotationRef.annotation().name().equalsIgnoreCase(annotation)) {

                return annotationRef;
            }
        }

        return null;
    }

    public abstract T get(Annotable target);
    public T get(Annotable annotable, T def) {

        T t = get(annotable);
        if (t == null ) {

            return def;
        } else {
            return t;
        }
    }

    public T get(V10GType type ) {

        return get(type.implementation());
    }

    public T get(V10GResource resource ) {

        return get(resource.implementation());
    }

    public T get(V10GMethod method ) {

        return get(method.implementation());
    }

    public T get(V10GResponse response ) {

        return get(response.implementation());
    }

    public T get(V10GType type, T def ) {

        return get(type.implementation(), def);
    }

}
