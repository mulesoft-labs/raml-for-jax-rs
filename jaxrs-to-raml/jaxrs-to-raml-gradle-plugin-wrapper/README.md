## Gradle Plugin
This plugin provides RAML code generation support to a Gradle project.

### Usage
To use it, simply include the required plugin dependency via `buildscript {}` and 'apply' the plugin:

```groovy
apply plugin: 'java'
apply plugin: 'jaxrstoraml'

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
    classpath "org.raml.jaxrs:jaxrs-to-raml-gradle-plugin:2.2.0-SNAPSHOT"
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

jaxrstoraml {
    inputPath = file("build/classes/main")
    outputDirectory = file("build")
    sourceDirectory = file('src/main/java')
    ramlFileName = file("build/api.raml")
}
```

## Configuration
The Gradle plugin supports the following configuration options, defined via the `jaxrsraml` closure:

|Property|Description|Required|
|:-------|:----------|--------|
|**inputPath**|The path to the directory containing JAX-RS class files.|**Yes**|
|**outputDirectory**|The output directory for the generated RAML files.|**Yes**|
|**sourceDirectory**|The path to the directory containing JAX-RS class files.|**Yes**|
|**ramlFileName**|Generated RAML file.|**Yes**|
|**translatedAnnotations**|List of translated annotations that should be translated|No|

## RAML Generation
Once the plugin has been applied and configured, execute the `jaxrstoraml` task to generated the JAX-RS resources:

```sh
  $ gradle wrapper --gradle-version 3.3
  $ ./gradlew compileJava jaxrstoraml
```

There is an example [here](../jaxrs-to-raml-examples/jaxrs-to-raml-gradle-examples/gradle-jaxrs-to-raml-annotations).
