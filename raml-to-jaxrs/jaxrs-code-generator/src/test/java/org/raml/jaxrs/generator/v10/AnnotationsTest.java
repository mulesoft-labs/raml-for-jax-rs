package org.raml.jaxrs.generator.v10;

import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.InputStreamReader;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 1/2/17.
 * Just potential zeroes and ones
 */
public class AnnotationsTest {
    @Test
    public void get() throws Exception {

        TypeDeclaration type = buildType(this, "annotations.raml", 0);
        assertEquals("Allo", Annotations.CLASS_NAME.get(type));
        assertEquals(true, Annotations.USE_PRIMITIVE_TYPE.get(type));
    }

    @Test
    public void getNotDefined() throws Exception {

        TypeDeclaration type = buildType(this, "annotations.raml", 1);
        assertEquals(false, Annotations.USE_PRIMITIVE_TYPE.get(type));

    }

    public static TypeDeclaration buildType(Object test, String raml, int index) {
        RamlModelResult ramlModelResult = new RamlModelBuilder()
                .buildApi(new InputStreamReader(test.getClass().getResourceAsStream(raml)), ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            return ramlModelResult.getApiV10().types().get(index);
        }
    }

}
