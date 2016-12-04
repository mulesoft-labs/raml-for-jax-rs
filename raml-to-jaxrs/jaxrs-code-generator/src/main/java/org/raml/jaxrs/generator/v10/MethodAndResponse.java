package org.raml.jaxrs.generator.v10;

import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.methods.Method;

/**
 * Created by Jean-Philippe Belanger on 12/3/16.
 * Just potential zeroes and ones
 */
public class MethodAndResponse {

    private Method method;
    private Response response;

    public MethodAndResponse(Method method, Response response) {
        this.method = method;
        this.response = response;
    }

    public Method getMethod() {
        return method;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MethodAndResponse that = (MethodAndResponse) o;

        if (!method.method().equals(that.method.method()))
            return false;
        return response.code().value().equals(that.response.code().value());
    }

    @Override
    public int hashCode() {
        int result = method.method().hashCode();
        result = 31 * result + response.code().value().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "{" + method.method() +
                ", " + response.code().value() +
                '}';
    }
}
