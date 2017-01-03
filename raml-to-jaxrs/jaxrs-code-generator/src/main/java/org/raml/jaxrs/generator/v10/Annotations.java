package org.raml.jaxrs.generator.v10;

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
}
