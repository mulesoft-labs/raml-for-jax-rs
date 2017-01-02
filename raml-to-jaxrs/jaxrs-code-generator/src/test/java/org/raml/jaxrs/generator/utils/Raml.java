package org.raml.jaxrs.generator.utils;

import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GAbstractionFactory;
import org.raml.jaxrs.generator.v10.V10Finder;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.resources.ResourceBuilder;
import org.raml.jaxrs.generator.v10.V10GResource;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Jean-Philippe Belanger on 12/25/16.
 * Just potential zeroes and ones
 */
public class Raml {
    public static Resource buildV10(Object test, String raml) {
        RamlModelResult ramlModelResult = new RamlModelBuilder()
                .buildApi(new InputStreamReader(test.getClass().getResourceAsStream(raml)), ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            return ramlModelResult.getApiV10().resources().get(0);
        }
    }

    public static void buildResource(Object test, String raml, CodeContainer<TypeSpec> container, String name, String uri) throws IOException {

        Resource resource  = buildV10(test, raml);
        CurrentBuild currentBuild = new CurrentBuild(null, "funk", "funk", "funk");
        ResourceBuilder builder = new ResourceBuilder(currentBuild, new V10GResource(new V10TypeRegistry(), new GAbstractionFactory(), resource),
                name, uri);
        builder.output(container);
    }

    public static CurrentBuild buildType(Object test, String raml, V10TypeRegistry registry, String name, String uri) throws IOException {

        RamlModelResult ramlModelResult = new RamlModelBuilder()
                .buildApi(new InputStreamReader(test.getClass().getResourceAsStream(raml)), ".");
        if (ramlModelResult.hasErrors()) {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults()) {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        } else {
            CurrentBuild currentBuild = new CurrentBuild(new V10Finder(ramlModelResult.getApiV10(), registry), "funk", "funk", "funk");
            currentBuild.constructClasses();
            return currentBuild;
        }
    }
}
