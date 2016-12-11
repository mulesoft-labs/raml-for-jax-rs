package org.raml.jaxrs.plugin;

import com.google.common.collect.ImmutableList;

import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;

class PluginConfiguration {
    private final ImmutableList<Path> inputPaths;
    private final Path outputDirectory;
    private final Path ramlFileName;

    private PluginConfiguration(ImmutableList<Path> inputPaths, Path outputDirectory, Path ramlFileName) {
        this.inputPaths = inputPaths;
        this.outputDirectory = outputDirectory;
        this.ramlFileName = ramlFileName;
    }

    public static PluginConfiguration create(Iterable<Path> inputPaths, Path outputDirectory, Path ramlFileName) {
        checkNotNull(inputPaths);

        return new PluginConfiguration(ImmutableList.copyOf(inputPaths), outputDirectory, ramlFileName);
    }

    public Iterable<Path> getInputPaths() {
        return inputPaths;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public Path getRamlFileName() {
        return ramlFileName;
    }
}
