package org.raml.jaxrs.generator.v10;

import org.junit.Test;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.GFinderListener;
import org.raml.jaxrs.generator.GRequest;
import org.raml.jaxrs.generator.GResponseType;
import org.raml.jaxrs.generator.GType;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 12/11/16.
 * Just potential zeroes and ones
 */
public class V10GResourceTest {

    @Test
    public void simpleRequest() throws Exception {

        Resource resource = build("resource-simple.raml");
        GAbstractionFactory fac = new GAbstractionFactory();
        V10GResource gr = new V10GResource(fac, resource);
        GRequest req = gr.methods().get(0).body().get(0);
        assertEquals("application/json", req.mediaType());
        assertEquals("ObjectBase", req.type().type());
        assertEquals("ObjectBase", req.type().name());
    }

    @Test
    public void extendingRequest() throws Exception {

        Resource resource = build("resource-extending-request.raml");
        GAbstractionFactory fac = new GAbstractionFactory();
        V10GResource gr = new V10GResource(fac, resource);
        GRequest req = gr.methods().get(0).body().get(0);
        assertEquals("application/json", req.mediaType());
        assertEquals("ObjectBase", req.type().type());
        assertEquals("/funputapplication/json", req.type().name());
        assertEquals("FunPutApplicationJson", req.type().defaultJavaTypeName());
    }

    @Test
    public void simpleResponse() throws Exception {

        Resource resource = build("resource-response-simple.raml");
        GAbstractionFactory fac = new GAbstractionFactory();
        V10GResource gr = new V10GResource(fac, resource);
        GResponseType resp = gr.methods().get(0).responses().get(0).body().get(0);
        assertEquals("application/json", resp.mediaType());
        assertEquals("ObjectBase", resp.type().type());
        assertEquals("ObjectBase", resp.type().name());
    }

    @Test
    public void extendingResponse() throws Exception {

        Resource resource = build("resource-response-extending.raml");
        GAbstractionFactory fac = new GAbstractionFactory();
        V10GResource gr = new V10GResource(fac, resource);
        GResponseType req = gr.methods().get(0).responses().get(0).body().get(0);
        assertEquals("application/json", req.mediaType());
        assertEquals("ObjectBase", req.type().type());
        assertEquals("/funputapplication/json", req.type().name());
        assertEquals("FunPut200ApplicationJson", req.type().defaultJavaTypeName());
    }

    private Resource build(String raml) {
        RamlModelResult ramlModelResult = new RamlModelBuilder()
                .buildApi(new InputStreamReader(this.getClass().getResourceAsStream(raml)), ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            return ramlModelResult.getApiV10().resources().get(0);
        }
    }
}
