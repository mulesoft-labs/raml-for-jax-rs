package org.raml.jaxrs.generator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/10/16.
 * Just potential zeroes and ones
 */
public class Main {

    public static void main(String[] args) throws IOException, GenerationException, ParseException {

        Options options = new Options();
        options.addOption("m", "model", true, "model package");
        options.addOption("s", "support", true, "support package");
        options.addOption("r", "resource", true, "resource package");
        options.addOption("e", "extensions", true, "extension options");
        options.addOption("d", "directory", true, "generation directory");

        CommandLineParser parser = new DefaultParser();
        CommandLine command = parser.parse(options, args);
        String modelDir = command.getOptionValue("m");
        String supportDir = command.getOptionValue("s");
        String resourceDir = command.getOptionValue("r");
        String directory = command.getOptionValue("d");
        String extensions = command.getOptionValue("e");

        List<String> ramlFiles =  command.getArgList();

        Configuration configuration = new Configuration();
        configuration.setModelPackage(modelDir);
        configuration.setResourcePackage(resourceDir);
        configuration.setSupportPackage(supportDir);
        configuration.setOutputDirectory(new File(directory));

        //configuration.setJsonMapper(AnnotationStyle.valueOf(jsonMapper.toUpperCase()));
        //configuration.setJsonMapperConfiguration(jsonMapperConfiguration);
        if ( extensions != null ) {
            configuration.setTypeConfiguration(extensions.split(("\\s*,\\s*")));
        }

        RamlScanner scanner = new RamlScanner(configuration);

        for (String ramlFile : ramlFiles) {

            scanner.handle(new File(ramlFile));
        }

        System.getProperties().remove("ramltojaxrs");
    }
}
