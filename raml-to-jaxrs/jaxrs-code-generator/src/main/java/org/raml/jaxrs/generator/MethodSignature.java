package org.raml.jaxrs.generator;

import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;

import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/12/16.
 * Just potential zeroes and ones
 */
public class MethodSignature {

    private final Method ramlMethod;
    private final TypeDeclaration mediaType;
    private final List<TypeDeclaration> requestParameters;
    private final List<TypeDeclaration> pathParameters;
    private  String cachedSignature ;

    public static MethodSignature signature(Method ramlMethod, List<TypeDeclaration> pathParameters,
            TypeDeclaration returnType) {

        return new MethodSignature(ramlMethod, ramlMethod.queryParameters(), pathParameters, returnType);
    }

    private MethodSignature(Method ramlMethod,
            List<TypeDeclaration> requestParameters, List<TypeDeclaration> pathParameters, TypeDeclaration returnType) {

        this.ramlMethod = ramlMethod;
        this.mediaType = returnType;
        this.requestParameters = requestParameters;
        this.pathParameters = pathParameters;
        cacheSignature();
    }

    private void cacheSignature() {

        StringBuilder builder = new StringBuilder();
        builder.append(ramlMethod.method()).append("(");
        for (TypeDeclaration requestParameter : requestParameters) {

            builder.append(requestParameter.type()).append(",");
        }

        for (TypeDeclaration pathParameter : pathParameters) {

            builder.append(pathParameter.type()).append(",");
        }

        if ( mediaType != null ) {
            builder.append(mediaType.type());
        }
        builder.append(")");
        cachedSignature = builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MethodSignature that = (MethodSignature) o;

        return cachedSignature.equals(that.cachedSignature);

    }

    @Override
    public int hashCode() {
        return cachedSignature.hashCode();
    }

    @Override
    public String toString() {
        return cachedSignature;
    }
}
