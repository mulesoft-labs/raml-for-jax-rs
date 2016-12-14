package org.raml.jaxrs.plugin;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.raml.emitter.RamlEmissionException;
import org.raml.jaxrs.converter.JaxRsToRamlConversionException;
import org.raml.jaxrs.converter.RamlConfiguration;
import org.raml.jaxrs.parser.JaxRsParsingException;
import org.raml.jaxrs.raml.core.DefaultRamlConfiguration;
import org.raml.jaxrs.raml.core.OneStopShop;
import org.raml.utilities.format.Joiners;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

@Mojo(name = "jaxrstoraml", requiresDependencyResolution = ResolutionScope.COMPILE)
public class JaxRsToRamlMojo extends AbstractMojo {

    @Parameter(property = "jaxrs.to.raml.input", defaultValue = "${project.build.directory}/classes")
    private File input;

    @Parameter(property = "jaxrs.to.raml.outputFileName", defaultValue = "${project.artifactId}.raml")
    private String outputFileName;

    @Parameter(property = "jaxrs.to.raml.outputDirectory", defaultValue = "${project.build.directory}")
    private String outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        PluginConfiguration configuration = createConfiguration();
        confinedExecute(configuration, getLog());
    }

    private static void confinedExecute(PluginConfiguration configuration, Log logger) throws MojoExecutionException {
        checkConfiguration(configuration);
        printConfiguration(configuration, logger);

        Path finalOutputFile = configuration.getOutputDirectory().resolve(configuration.getRamlFileName());
        String applicationName = FilenameUtils.removeExtension(configuration.getRamlFileName().getFileName().toString());


        Path jaxRsUrl = Iterables.get(configuration.getInputPaths(), 0);

        RamlConfiguration ramlConfiguration = DefaultRamlConfiguration.forApplication(applicationName);
        try {
            OneStopShop.create().parseJaxRsAndOutputRaml(jaxRsUrl, finalOutputFile, ramlConfiguration);
        } catch (JaxRsToRamlConversionException | JaxRsParsingException | RamlEmissionException e) {
            throw new MojoExecutionException(format("unable to generate output raml file: %s", finalOutputFile), e);
        }
    }

    private PluginConfiguration createConfiguration() {
        return PluginConfiguration.create(getInputFiles(), getOutputDirectoryPath(), getRamlFileName());
    }

    private static void printConfiguration(PluginConfiguration configuration, Log logger) {
        logger.info("Configuration");
        logger.info(format("input paths: %s", Joiners.squareBracketsSameLineJoiner().join(configuration.getInputPaths())));
        logger.info(format("output directory: %s", configuration.getOutputDirectory()));
        logger.info(format("output file name: %s", configuration.getRamlFileName()));
    }

    private static void checkConfiguration(PluginConfiguration configuration) throws MojoExecutionException {
        checkInputFiles(configuration.getInputPaths());
    }

    private static void checkInputFiles(Iterable<Path> inputFiles) throws MojoExecutionException {
        for (Path inputPath : inputFiles) {
            //Check that input is an existing file, otherwise fail.
            if (!Files.isRegularFile(inputPath) && !Files.isDirectory(inputPath)) {
                throw new MojoExecutionException(format("invalid input file: %s", inputPath));
            }
        }
    }

    private Iterable<Path> getInputFiles() {
        Path inputPath = Paths.get(input.getAbsolutePath());

        List<Path> pathList = Lists.newArrayList();
        pathList.add(inputPath);

        return pathList;
    }

    public Path getOutputDirectoryPath() {
        return Paths.get(outputDirectory);
    }

    public Path getRamlFileName() {
        return Paths.get(outputFileName);
    }
}
