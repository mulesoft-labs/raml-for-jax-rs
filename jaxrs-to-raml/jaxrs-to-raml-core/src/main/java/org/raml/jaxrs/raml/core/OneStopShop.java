package org.raml.jaxrs.raml.core;

import org.raml.emitter.Emitter;
import org.raml.emitter.FileEmitter;
import org.raml.emitter.RamlEmissionException;
import org.raml.jaxrs.converter.JaxRsToRamlConversionException;
import org.raml.jaxrs.converter.JaxRsToRamlConverter;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.JaxRsParser;
import org.raml.jaxrs.parser.JaxRsParsingException;
import org.raml.model.RamlApi;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OneStopShop {

    private OneStopShop() {
    }

    public static OneStopShop create() {
        return new OneStopShop();
    }

    public void parseJaxRsAndOutputRaml(Path jaxRsUrl, Path ramlOutputFile, RamlConfiguration configuration) throws JaxRsToRamlConversionException, JaxRsParsingException, RamlEmissionException {

        JaxRsApplication application = JaxRsParser.create().parse(jaxRsUrl);

        RamlApi ramlApi = JaxRsToRamlConverter.create().convert(configuration, application);

        Emitter emitter = FileEmitter.forFile(ramlOutputFile);

        emitter.emit(ramlApi);
    }

    //For testing
    public static void main(String[] args) throws JaxRsToRamlConversionException, JaxRsParsingException, RamlEmissionException {
        Path jaxRsResourceFile = Paths.get(args[0]);
        Path ramlOutputFile = Paths.get(args[1]);

        OneStopShop oneStopShop = OneStopShop.create();

        RamlConfiguration ramlConfiguration = DefaultRamlConfiguration.forApplication(jaxRsResourceFile.getFileName().toString());

        oneStopShop.parseJaxRsAndOutputRaml(jaxRsResourceFile, ramlOutputFile, ramlConfiguration);
    }

}
