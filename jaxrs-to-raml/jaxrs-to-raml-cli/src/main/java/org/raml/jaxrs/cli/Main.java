package org.raml.jaxrs.cli;

import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.raml.core.DefaultRamlConfiguration;
import org.raml.jaxrs.raml.core.OneStopShop;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws Exception {
        Path jaxRsResourceFile = Paths.get(args[0]);
        Path ramlOutputFile = Paths.get(args[1]);

        RamlConfiguration ramlConfiguration = DefaultRamlConfiguration.forApplication(jaxRsResourceFile.getFileName().toString());

        OneStopShop oneStopShop =
                OneStopShop.builder()
                        .withJaxRsClassesRoot(jaxRsResourceFile)
                        .withRamlOutputFile(ramlOutputFile)
                        .withRamlConfiguration(ramlConfiguration)
                        .build();

        oneStopShop.parseJaxRsAndOutputRaml();
    }
}
