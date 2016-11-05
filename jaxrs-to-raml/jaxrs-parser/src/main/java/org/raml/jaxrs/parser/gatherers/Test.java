package org.raml.jaxrs.parser.gatherers;

import javax.ws.rs.Path;

@Path("tutu")
public class Test {

    static {
        System.out.println("toto");
    }

    static String getName() {
        return "x";
    }
}
