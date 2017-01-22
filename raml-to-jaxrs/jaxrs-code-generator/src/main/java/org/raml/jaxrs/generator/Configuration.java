package org.raml.jaxrs.generator;

import org.jsonschema2pojo.AnnotationStyle;
import org.jsonschema2pojo.GenerationConfig;
import org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension;
import org.raml.jaxrs.generator.extension.types.TypeExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 12/25/16.
 * Just potential zeroes and ones
 */
public class Configuration {


    private String modelPackage;
    private File outputDirectory;
    private AnnotationStyle jsonMapper;
    private Map<String, String> jsonMapperConfiguration = new HashMap<>();
    private String[] typeConfiguration = new String[0];
    private String resourcePackage;
    private String supportPackage;
    private List<TypeExtension> typeExtensions = new ArrayList<>();

    private Class<GlobalResourceExtension> defaultCreationExtension;
    private Class<GlobalResourceExtension> defaultFinishExtension;


    public void setupBuild(CurrentBuild build) {

        build.setConfiguration(this);
    }

    public void setJsonMapper(AnnotationStyle jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public void setJsonMapperConfiguration(Map<String, String> jsonMapperConfiguration) {
        this.jsonMapperConfiguration = jsonMapperConfiguration;
    }

    public String getSupportPackage() {
        if ( supportPackage == null ) {
            return resourcePackage;
        }
        return supportPackage;
    }

    public void setSupportPackage(String supportPackage) {
        this.supportPackage = supportPackage;
    }

    public String getModelPackage() {

        if ( modelPackage == null ) {
            return resourcePackage;
        }
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public String getResourcePackage() {
        return resourcePackage;
    }

    public void setResourcePackage(String resourcePackage) {
        this.resourcePackage = resourcePackage;
    }

    public String[] getTypeConfiguration() {
        return typeConfiguration;
    }

    public void setTypeConfiguration(String[] typeConfiguration) {

        this.typeConfiguration = typeConfiguration;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public static Configuration defaultConfiguration() {

        Configuration configuration = new Configuration();
        configuration.setModelPackage("model");
        configuration.setResourcePackage("resource");
        configuration.setSupportPackage("support");
        configuration.setOutputDirectory(new File("."));
//        configuration.setJsonMapper(AnnotationStyle.valueOf(jsonMapper.toUpperCase()));
//        configuration.setJsonMapperConfiguration(jsonMapperConfiguration);
        configuration.setTypeConfiguration(new String[] {"jackson"});

        return configuration;

    }

    public List<TypeExtension> getTypeExtensions() {
        return typeExtensions;
    }

    public GenerationConfig createJsonSchemaGenerationConfig()
    {
        return new RamlToJaxRSGenerationConfig(jsonMapper, jsonMapperConfiguration);
    }

    public void defaultResourceCreationExtension(Class<GlobalResourceExtension> c) {
        defaultCreationExtension = c;
    }

    public void defaultResourceFinishExtension(Class<GlobalResourceExtension> c) {
        defaultFinishExtension = c;
    }

    public Class<GlobalResourceExtension> getDefaultCreationExtension() {
        return defaultCreationExtension;
    }

    public Class<GlobalResourceExtension> getDefaultFinishExtension() {
        return defaultFinishExtension;
    }
}
