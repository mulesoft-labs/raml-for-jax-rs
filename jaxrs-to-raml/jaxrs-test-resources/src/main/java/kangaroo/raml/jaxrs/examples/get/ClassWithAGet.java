package kangaroo.raml.jaxrs.examples.get;

import javax.ws.rs.GET;

/**
 *
 */
public class ClassWithAGet {

    @GET
    public String hello() {
        return "hello";
    }
}
