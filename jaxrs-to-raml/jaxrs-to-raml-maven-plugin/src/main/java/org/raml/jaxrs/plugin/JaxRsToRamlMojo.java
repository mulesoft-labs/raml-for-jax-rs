package org.raml.jaxrs.plugin;

import com.google.common.collect.Lists;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.raml.utilities.format.Joiners;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.lang.String.format;

@Mojo(name = "jaxrstoraml")
public class JaxRsToRamlMojo extends AbstractMojo {

    @Parameter(property = "jaxrs.to.raml.input", required = true)
    private File input;

    @Parameter(property = "jaxrs.to.raml.outputFileName", defaultValue = "${project.artifactId}.raml")
    private String outputFileName;

    @Parameter(property = "jaxrs.to.raml.outputDirectory", defaultValue = "${project.build.directory}")
    private String outputDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        PluginConfiguration configuration = createConfiguration();
        checkConfiguration(configuration);
        printConfiguration(configuration);

    }

    private PluginConfiguration createConfiguration() {
        return PluginConfiguration.create(getInputFiles(), getOutputDirectoryPath(), getRamlFileName());
    }

    private void printConfiguration(PluginConfiguration configuration) {
        getLog().info("Configuration");
        getLog().info(format("input paths: %s", Joiners.squareBracketsSameLineJoiner().join(configuration.getInputPaths())));
        getLog().info(format("output directory: %s", outputDirectory));
        getLog().info(format("output file name: %s", outputFileName));
    }

    private void checkConfiguration(PluginConfiguration configuration) throws MojoExecutionException {
        checkInputFiles(configuration.getInputPaths());
    }

    private static void checkInputFiles(Iterable<Path> inputFiles) throws MojoExecutionException {
        for (Path inputPath : inputFiles) {
            //Check that input is an existing file, otherwise fail.
            if (!Files.isRegularFile(inputPath)) {
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
