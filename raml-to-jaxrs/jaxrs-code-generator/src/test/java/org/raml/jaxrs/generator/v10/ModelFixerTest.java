package org.raml.jaxrs.generator.v10;

import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 11/14/16.
 * Just potential zeroes and ones
 */
public class ModelFixerTest {

    @Test
    public void simpleInheritedTypes() {

        RamlModelResult result = new RamlModelBuilder()
                .buildApi(new InputStreamReader(this.getClass().getResourceAsStream("/simple_inheritance.raml")), ".");

        TypeDeclaration declaration = result.getApiV10().types().get(1);
        List<TypeDeclaration> parents =  ModelFixer.parentTypes(result.getApiV10().types(), declaration);

        assertEquals(1, parents.size());
        assertEquals("Human", parents.get(0).name());
    }

    @Test
    public void multipleInheritance() {

        RamlModelResult result = new RamlModelBuilder()
                .buildApi(new InputStreamReader(this.getClass().getResourceAsStream("/multiple_inheritance.raml")), ".");

        TypeDeclaration declaration = result.getApiV10().types().get(2);
        List<TypeDeclaration> parents =  ModelFixer.parentTypes(result.getApiV10().types(), declaration);

        assertEquals(2, parents.size());
        assertEquals("Human", parents.get(0).name());
        assertEquals("Job", parents.get(1).name());
    }

}
