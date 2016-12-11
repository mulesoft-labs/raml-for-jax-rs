package org.raml.jaxrs.generator.v10;

import com.google.common.collect.Multimap;
import org.raml.jaxrs.generator.GMethod;
import org.raml.jaxrs.generator.GRequest;
import org.raml.jaxrs.generator.GResource;
import org.raml.jaxrs.generator.GResponse;
import org.raml.jaxrs.generator.GType;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

/**
 * Created by Jean-Philippe Belanger on 12/4/16.
 * Just potential zeroes and ones
 */
public class ResourceUtils {

    public static void fillInBodiesAndResponses(GResource resource, Multimap<GMethod, GType> incomingBodies, Multimap<GMethod, GResponse> responses) {

/*
        for (GMethod method : resource.methods()) {

            if ( method.body().size() == 0 ) {
                incomingBodies.put(method, null);
            } else {
                for (GRequest typeDeclaration : method.body()) {

                    incomingBodies.put(method, typeDeclaration);
                }
            }

            if ( method.responses().size() == 0 ) {
                incomingBodies.put(method, null);
            } else {
                for (GResponse response : method.responses()) {

                        responses.put(method, response);
                }
            }
        }
*/
    }
}
