package org.raml.jaxrs.generator.v10;

import com.google.common.collect.Multimap;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 12/4/16.
 * Just potential zeroes and ones
 */
public class ResourceUtils {

    public static void fillInBodiesAndResponses(Resource resource, Multimap<Method, TypeDeclaration> incomingBodies, Multimap<Method, Response> responses) {

        for (Method method : resource.methods()) {

            if ( method.body().size() == 0 ) {
                incomingBodies.put(method, null);
            } else {
                for (TypeDeclaration typeDeclaration : method.body()) {

                    incomingBodies.put(method, typeDeclaration);
                }
            }

            if ( method.responses().size() == 0 ) {
                incomingBodies.put(method, null);
            } else {
                for (Response response : method.responses()) {

                        responses.put(method, response);
                }
            }
        }
    }
}
