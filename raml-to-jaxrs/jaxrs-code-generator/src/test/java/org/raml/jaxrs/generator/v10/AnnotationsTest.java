package org.raml.jaxrs.generator.v10;

import org.junit.Test;
import org.raml.jaxrs.generator.builders.extensions.resources.TrialResourceExtension;
import org.raml.jaxrs.generator.builders.extensions.resources.TrialResourceMethodExtension;
import org.raml.jaxrs.generator.builders.extensions.resources.TrialResponseClassExtension;
import org.raml.jaxrs.generator.builders.extensions.resources.TrialResponseMethodExtension;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void getResponseClass() throws Exception {

        Api type = buildApi(this, "annotations.raml", 0);

        assertTrue(Annotations.ON_RESOURCE_CLASS_CREATION.get(type.resources().get(0)) instanceof TrialResourceExtension );
        assertTrue(Annotations.ON_METHOD_CREATION.get(type.resources().get(0).methods().get(0)) instanceof TrialResourceMethodExtension);
        assertTrue(Annotations.ON_RESPONSE_CLASS_CREATION.get(type.resources().get(0).methods().get(0)) instanceof TrialResponseClassExtension);
        assertTrue(Annotations.ON_RESPONSE_METHOD_CREATION.get(type.resources().get(0).methods().get(0).responses().get(0)) instanceof TrialResponseMethodExtension);
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

    public static Api buildApi(Object test, String raml, int index) {
        RamlModelResult ramlModelResult = new RamlModelBuilder()
                .buildApi(new InputStreamReader(test.getClass().getResourceAsStream(raml)), ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            return ramlModelResult.getApiV10();
        }
    }

}
