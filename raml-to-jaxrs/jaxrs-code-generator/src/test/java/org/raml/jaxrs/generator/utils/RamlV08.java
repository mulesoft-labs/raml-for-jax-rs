package org.raml.jaxrs.generator.utils;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.resources.ResourceBuilder;
import org.raml.jaxrs.generator.v08.V08Finder;
import org.raml.jaxrs.generator.v08.V08GResource;
import org.raml.jaxrs.generator.v08.V08TypeRegistry;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v08.api.Api;
import org.raml.v2.api.model.v08.resources.Resource;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Jean-Philippe Belanger on 12/25/16.
 * Just potential zeroes and ones
 */
public class RamlV08 {

    public static Resource withV08(Object test, String raml) {

        return buildApiV08(test, raml).resources().get(0);
    }

    public static Api buildApiV08(Object test, String raml) {

        RamlModelResult ramlModelResult = new RamlModelBuilder()
                .buildApi(new InputStreamReader(test.getClass().getResourceAsStream(raml)), ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            return ramlModelResult.getApiV08();
        }
    }

    public static void buildResourceV08(Object test, String raml, CodeContainer<TypeSpec> container, String name, String uri) throws IOException {

        Api api  = buildApiV08(test, raml);
        V08TypeRegistry registry = new V08TypeRegistry();
        V08Finder typeFinder = new V08Finder(api, new GAbstractionFactory(), registry);
        CurrentBuild currentBuild = new CurrentBuild(typeFinder);
        currentBuild.constructClasses();
        ResourceBuilder builder = new ResourceBuilder(currentBuild, new V08GResource(new GAbstractionFactory(), api.resources().get(0), typeFinder.globalSchemas(), registry),
                name, uri);
        builder.output(container);
    }
}
