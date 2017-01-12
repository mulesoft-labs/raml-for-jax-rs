package org.raml.jaxrs.generator;

import com.google.common.collect.Multimap;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GRequest;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.jaxrs.generator.ramltypes.GResponse;

/**
 * Created by Jean-Philippe Belanger on 12/4/16.
 * Just potential zeroes and ones
 */
public class ResourceUtils {

    public static void fillInBodiesAndResponses(GResource resource, Multimap<GMethod, GRequest> incomingBodies, Multimap<GMethod, GResponse> responses) {


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

    }
}
