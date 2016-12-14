package org.raml.jaxrs.plugin;

import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

class PluginConfiguration {
    private final Path inputPaths;
    private final Path sourceDirectory;
    private final Path outputDirectory;
    private final Path ramlFileName;

    private PluginConfiguration(Path inputPaths, Path sourceDirectory, Path outputDirectory, Path ramlFileName) {
        this.inputPaths = inputPaths;
        this.sourceDirectory = sourceDirectory;
        this.outputDirectory = outputDirectory;
        this.ramlFileName = ramlFileName;
    }

    public static PluginConfiguration create(Path inputPath, Path sourceDirectory, Path outputDirectory, Path ramlFileName) {
        checkNotNull(inputPath);
        checkNotNull(sourceDirectory);
        checkNotNull(outputDirectory);
        checkNotNull(ramlFileName);

        return new PluginConfiguration(inputPath, sourceDirectory, outputDirectory, ramlFileName);
    }

    public Path getInput() {
        return inputPaths;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public Path getRamlFileName() {
        return ramlFileName;
    }

    public Path getSourceDirectory() {
        return sourceDirectory;
    }
}
