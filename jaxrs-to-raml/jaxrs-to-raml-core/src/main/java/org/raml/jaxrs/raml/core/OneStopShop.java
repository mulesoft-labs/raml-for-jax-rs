package org.raml.jaxrs.raml.core;

import org.raml.api.RamlApi;
import org.raml.emitter.Emitter;
import org.raml.emitter.FileEmitter;
import org.raml.emitter.RamlEmissionException;
import org.raml.jaxrs.converter.JaxRsToRamlConversionException;
import org.raml.jaxrs.converter.JaxRsToRamlConverter;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.JaxRsParsers;
import org.raml.jaxrs.parser.JaxRsParsingException;
import org.raml.utilities.builder.NonNullableField;

import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.raml.utilities.builder.Preconditions.checkSet;
import static org.raml.utilities.builder.Preconditions.checkUnset;

public class OneStopShop {

    private final Path jaxRsUrl;
    private final Path ramlOutputFile;
    private final NonNullableField sourceCodeRoot;
    private final RamlConfiguration ramlConfiguration;

    private OneStopShop(Path jaxRsUrl, Path ramlOutputFile, NonNullableField sourceCodeRoot, RamlConfiguration ramlConfiguration) {
        this.jaxRsUrl = jaxRsUrl;
        this.ramlOutputFile = ramlOutputFile;
        this.sourceCodeRoot = sourceCodeRoot;
        this.ramlConfiguration = ramlConfiguration;
    }

    private static OneStopShop create(Path jaxRsUrl, Path ramlOutputFile, NonNullableField sourceCodeRoot, RamlConfiguration ramlConfiguration) {
        checkNotNull(jaxRsUrl);
        checkNotNull(ramlOutputFile);
        checkNotNull(sourceCodeRoot);
        checkNotNull(ramlConfiguration);

        return new OneStopShop(jaxRsUrl, ramlOutputFile, sourceCodeRoot, ramlConfiguration);
    }

    public void parseJaxRsAndOutputRaml() throws JaxRsToRamlConversionException, JaxRsParsingException, RamlEmissionException {

        JaxRsApplication application = JaxRsParsers.usingJerseyForPath(jaxRsUrl).parse();

        RamlApi ramlApi = JaxRsToRamlConverter.create().convert(ramlConfiguration, application);

        Emitter emitter = FileEmitter.forFile(ramlOutputFile);

        emitter.emit(ramlApi);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        NonNullableField<Path> jaxRsClassesRoot = NonNullableField.unset();
        NonNullableField<Path> sourceCodeRoot = NonNullableField.unset(); //Optional field.
        NonNullableField<Path> ramlOutputFile = NonNullableField.unset();
        NonNullableField<RamlConfiguration> ramlConfiguration = NonNullableField.unset();

        private Builder() {}

        public Builder withJaxRsClassesRoot(Path jaxRsClassesRoot) {
            checkUnset(this.jaxRsClassesRoot, "classes root");

            this.jaxRsClassesRoot = NonNullableField.of(jaxRsClassesRoot);

            return this;
        }

        public Builder withSourceCodeRoot(Path sourceCodeRoot) {
            checkUnset(this.sourceCodeRoot, "source code root");

            this.sourceCodeRoot = NonNullableField.of(sourceCodeRoot);

            return this;
        }

        public Builder withRamlOutputFile(Path ramlOutputFile) {
            checkUnset(this.ramlOutputFile, "raml output file");

            this.ramlOutputFile = NonNullableField.of(ramlOutputFile);

            return this;
        }

        public Builder withRamlConfiguration(RamlConfiguration ramlConfiguration) {
            checkUnset(this.ramlConfiguration, "raml configuration");

            this.ramlConfiguration = NonNullableField.of(ramlConfiguration);

            return this;
        }

        public OneStopShop build() {
            checkSet(this.jaxRsClassesRoot, "classes root");
            checkSet(this.ramlOutputFile, "raml output file");
            checkSet(this.ramlConfiguration, "raml configuration");

            return OneStopShop.create(this.jaxRsClassesRoot.get(), this.ramlOutputFile.get(), this.sourceCodeRoot, this.ramlConfiguration.get());
        }

    }

}
