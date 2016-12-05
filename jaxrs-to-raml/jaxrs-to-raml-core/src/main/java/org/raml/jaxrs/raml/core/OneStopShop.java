package org.raml.jaxrs.raml.core;

import org.raml.emitter.Emitter;
import org.raml.emitter.FileEmitter;
import org.raml.emitter.RamlEmissionException;
import org.raml.jaxrs.converter.JaxRsToRamlConversionException;
import org.raml.jaxrs.converter.JaxRsToRamlConverter;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.JaxRsParsers;
import org.raml.jaxrs.parser.JaxRsParsingException;
import org.raml.api.RamlApi;

import java.nio.file.Path;

public class OneStopShop {

    private OneStopShop() {
    }

    public static OneStopShop create() {
        return new OneStopShop();
    }

    public void parseJaxRsAndOutputRaml(Path jaxRsUrl, Path ramlOutputFile, RamlConfiguration configuration) throws JaxRsToRamlConversionException, JaxRsParsingException, RamlEmissionException {

        JaxRsApplication application = JaxRsParsers.usingJersey().parse(jaxRsUrl);

        RamlApi ramlApi = JaxRsToRamlConverter.create().convert(configuration, application);

        Emitter emitter = FileEmitter.forFile(ramlOutputFile);

        emitter.emit(ramlApi);
    }

}
