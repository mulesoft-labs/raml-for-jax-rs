package org.raml.jaxrs.examples.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("resource/with/doc")
public class ResourceWithDoc {

    /**
     * This post operation does nothing.
     *
     * Multiline blablabla.
     */
    @POST
    public void thePost() {}

    /**
     * This get method does even less, can you imagine?
     *
     * I can.
     *
     * I made it so.
     *
     * @param none Just make sure this doesn't show.
     * @return Neither does this.
     * @throws RuntimeException Nor this.
     */
    @GET
    public String theGet() {
        return "coucou";
    }

}
