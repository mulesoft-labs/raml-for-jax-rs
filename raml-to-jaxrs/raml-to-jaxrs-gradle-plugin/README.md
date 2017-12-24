## Gradle Plugin
This plugin provides RAML code generation support to a Gradle project.

### Usage
To use it, simply include the required plugin dependency via `buildscript {}` and 'apply' the plugin:

```groovy
apply plugin: 'java'
apply plugin: 'ramltojaxrs'

group = 'example'
version = '0.1'

buildscript {
  repositories {
    mavenLocal()
    maven { url 'https://repository.mulesoft.org/snapshots/' }
    maven { url 'https://repository.mulesoft.org/releases/' }
    mavenCentral()
  }
  dependencies {
    classpath "org.raml.jaxrs:raml-to-jaxrs-gradle-plugin:2.2.0-SNAPSHOT"
  }
}

repositories {
     mavenLocal()
     maven { url 'https://repository.mulesoft.org/snapshots/' }
     maven { url 'https://repository.mulesoft.org/releases/' }
     mavenCentral()
}

dependencies {
    // Your project's dependencies here
}

ramltojaxrs {
    sourceDirectory = file('src/main/resources')
    outputDirectory = new File(buildDir,'generated')
    supportPackageName = 'org.raml.test.gen'
    resourcePackageName = 'org.raml.test.res'
    modelPackageName = 'org.raml.test.model'
}
```

### Configuration
The Gradle plugin supports the following configuration options, defined via the `ramltojaxrs` closure:

|Property|Description|Required|
|:-------|:----------|--------|
|**sourceDirectory**|The path to the directory containing source .raml files|**Yes**|
|**outputDirectory**|The output directory for the generated JAX-RS resource source files.|**Yes**|
|**supportPackageName**|The package used for support classes.|**Yes**|
|**resourcePackageName**|The package used for resource classes.|**Yes**|
|**modelPackageName**|The package used for type classes.|**Yes**|
|**jsonMapper**|The annotation style used for jsonschema objects (jsonschema2pojo)|No|
|**jsonMapperConfiguration**|Options for jsonschema objects (jsonschema2pojo)|No|
|**generateTypesWith**|options for annotating RAML types|No, but honestly if you put nothing in here...|


### Code Generation
Once the plugin has been applied and configured, execute the `ramltojaxrs` task to generated the JAX-RS resources:

```sh
  $ gradle wrapper --gradle-version 3.3
  $ ./gradlew compileJava ramltojaxrs
```

There is an example [here](../examples/gradle-examples/gradle-jaxb-example).
