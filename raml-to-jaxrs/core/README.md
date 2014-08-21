![](http://raml.org/images/logo.png)

# RAML JAX-RS Codegen - Core Generator

API developers should seldom have to use the core generator but instead should use a plug-in for their build tool.

## Usage

Example:

    File outputDirectory = new File("/some/path/to/target/code-gen");

    Configuration configuration = new Configuration();
    configuration.setOutputDirectory(outputDirectory);
    configuration.setSourceDirectory(inputDirectory)
    configuration.setBasePackageName("org.raml.jaxrs.test");

    InputStreamReader ramlReader = new InputStreamReader(getClass().getResourceAsStream("/org/raml/full-config-with-patch.yaml"));

    new Generator().run(ramlReader, configuration);

