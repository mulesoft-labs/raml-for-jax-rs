package kangaroo.raml.jaxrs.examples.post;

import javax.ws.rs.POST;

/**
 *
 */
public class ClassWithAPost {

    static {
        System.out.println("toto");
    }

    @POST
    public void post() {}
}
