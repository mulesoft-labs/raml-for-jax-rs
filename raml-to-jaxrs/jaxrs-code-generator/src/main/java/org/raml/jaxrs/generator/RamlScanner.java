package org.raml.jaxrs.generator;


import org.raml.jaxrs.generator.v08.V08Finder;
import org.raml.jaxrs.generator.v10.ResourceHandler;
import org.raml.jaxrs.generator.v10.V10Finder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.v10.resources.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


/**
 * Created by Jean-Philippe Belanger on 10/26/16.
 * Just potential zeroes and ones
 */
public class RamlScanner {

    private final String destDir;
    private final String packageName;
    private final String modelPackageName;
    private final String supportPackage;

    public RamlScanner(String destDir, String packageName, String modelPackageName, String supportPackage) {
        this.destDir = destDir;
        this.packageName = packageName;
        this.modelPackageName = modelPackageName;
        this.supportPackage = supportPackage;
    }

    public RamlScanner(String destDir, String packageName) {
        this.destDir = destDir;
        this.packageName = packageName;
        this.modelPackageName = packageName;
        this.supportPackage =  packageName;
    }

    public void handle(String resourceName) throws IOException, GenerationException {

        handle(RamlScanner.class.getResource(resourceName), ".");
    }

    public void handle(String resourceName, String directory) throws IOException, GenerationException {

        handle(RamlScanner.class.getResource(resourceName), directory);
    }

    public void handle(File resource) throws IOException, GenerationException {

        handle(new FileInputStream(resource), resource.getParentFile().getAbsolutePath() + "/");
    }

    public void handle(URL resourceName, String directory) throws IOException, GenerationException {

        handle(resourceName.openStream(), directory);
    }

    public void handle(InputStream stream, String directory) throws GenerationException, IOException {

        RamlModelResult result = new RamlModelBuilder().buildApi(new InputStreamReader(stream), directory);
        if ( result.hasErrors() ) {
            throw new GenerationException(result.getValidationResults());
        }

        if ( result.isVersion08() ) {
            handle(result.getApiV08());
        } else {
            handle(result.getApiV10());
        }
    }

    public void handle(org.raml.v2.api.model.v10.api.Api api) throws IOException {

        V10TypeRegistry registry = new V10TypeRegistry();
        GAbstractionFactory factory = new GAbstractionFactory();
        CurrentBuild build = new CurrentBuild(new V10Finder(api, registry), packageName, modelPackageName, supportPackage);
        Configuration configuration = Configuration.createConfiguration(System.getProperty("ramltojaxrs"));
        configuration.setupBuild(build);
        build.constructClasses(new TypeFactory(build, factory, registry));

        ResourceHandler resourceHandler = new ResourceHandler(build);


        // handle resources.
        for (Resource resource : api.resources()) {
            resourceHandler.handle(registry, resource);
        }


        build.generate(destDir);
    }


    public void handle(org.raml.v2.api.model.v08.api.Api api) throws IOException {

        GAbstractionFactory factory = new GAbstractionFactory();
        V08Finder typeFinder = new V08Finder(api, factory);
        CurrentBuild build = new CurrentBuild(typeFinder, packageName, modelPackageName, supportPackage);
        Configuration configuration = Configuration.createConfiguration(System.getProperty("ramltojaxrs"));
        configuration.setupBuild(build);

        build.constructClasses(new TypeFactory(build, factory, null));

        ResourceHandler resourceHandler = new ResourceHandler(build);


        // handle resources.
        for (org.raml.v2.api.model.v08.resources.Resource resource : api.resources()) {
            resourceHandler.handle(typeFinder.globalSchemas(), resource);
        }

        build.generate(destDir);
    }

}
