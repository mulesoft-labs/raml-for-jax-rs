package org.raml.jaxrs.generator.v10;

import org.junit.Test;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.GRequest;
import org.raml.jaxrs.generator.GResponseType;
import org.raml.jaxrs.generator.utils.Raml;
import org.raml.v2.api.model.v10.resources.Resource;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V10GResourceTest {

    @Test
    public void simpleRequest() throws Exception {

        Resource resource = Raml.buildV10(this, "resource-simple.raml");
        GAbstractionFactory fac = new GAbstractionFactory();
        V10GResource gr = new V10GResource(new V10TypeRegistry(), fac, resource);
        GRequest req = gr.methods().get(0).body().get(0);
        assertEquals("application/json", req.mediaType());
        assertEquals("ObjectBase", req.type().type());
        assertEquals("ObjectBase", req.type().name());
    }

    @Test
    public void extendingRequest() throws Exception {

        Resource resource = Raml.buildV10(this, "resource-extending-request.raml");
        GAbstractionFactory fac = new GAbstractionFactory();
        V10GResource gr = new V10GResource(new V10TypeRegistry(), fac, resource);
        GRequest req = gr.methods().get(0).body().get(0);
        assertEquals("application/json", req.mediaType());
        assertEquals("ObjectBase", req.type().type());
        assertEquals("/funputapplication/json", req.type().name());
        assertEquals("FunPutApplicationJson", req.type().defaultJavaTypeName("").toString());
    }

    @Test
    public void simpleResponse() throws Exception {

        Resource resource = Raml.buildV10(this, "resource-response-simple.raml");
        GAbstractionFactory fac = new GAbstractionFactory();
        V10GResource gr = new V10GResource(new V10TypeRegistry(), fac, resource);
        GResponseType resp = gr.methods().get(0).responses().get(0).body().get(0);
        assertEquals("application/json", resp.mediaType());
        assertEquals("ObjectBase", resp.type().type());
        assertEquals("ObjectBase", resp.type().name());
    }

    @Test
    public void extendingResponse() throws Exception {

        Resource resource = Raml.buildV10(this, "resource-response-extending.raml");
        GAbstractionFactory fac = new GAbstractionFactory();
        V10GResource gr = new V10GResource(new V10TypeRegistry(), fac, resource);
        GResponseType req = gr.methods().get(0).responses().get(0).body().get(0);
        assertEquals("application/json", req.mediaType());
        assertEquals("ObjectBase", req.type().type());
        assertEquals("/funput200application/json", req.type().name());
        assertEquals("FunPut200ApplicationJson", req.type().defaultJavaTypeName("").toString());
    }

}
