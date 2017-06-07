![](http://raml.org/images/logo.png)

# RAML to JAX-RS codegen - Gradle Plug-in

This plugin provides RAML code generation support to a Gradl project.

## Usage

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
    classpath "org.raml:raml-gradle-plugin:1.0-SNAPSHOT"
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

## Configuration

The Gradle plugin supports the following configuration options, defined via the `raml` closure:

|Property|Description|Default Value|Required|
|:-------|:----------|:------------|--------|
|**basePackageName**|The base Java package name used for the generated JAX-RS resource files.| |**Yes**|
|**jsonMapper**|The JSON mapper target version|*JACKSON1*|No|
|**outputDirectory**|The output directory for the generated JAX-RS resource source files.|*$project.buildDir/generated-sources/raml-jaxrs*|No|
|**sourceDirectory**|The path to the directory containing source .raml and .yaml files.|*$project.rootDir/src/main/raml*|No|
|**sourcePaths**|The set of source .raml and .yaml files in addition to those found in the source directory.| |No|
|**modelPackageName**|Name of package containing generated model classes|model|No|
|**resourcePackageName**|Name of package containing generated model resources| |Yes|
|**supportPackageName**|Name of package containing generated support classes| |Yes|
|**generateTypeWith**|Annotate raml 1.0 types with either jackson, jaxb or gson| |No|
|**resourceCreationExtension**|The name of a generator extension class (implements org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension)| |No|
|**typeExtensions**|The name of a generator extension class (implements org.raml.jaxrs.generator.extension.resources.GlobalResourceExtension)| |No|
|**resourceFinishExtension**|The name of a generator extension class (implements org.raml.jaxrs.generator.extension.resources.LegacyTypeExtension)| |No|

For example:

```groovy
ramltojaxrs {
    sourceDirectory = file('src/main/resources')
    outputDirectory = new File(buildDir,'generated')
    supportPackageName = 'org.raml.test.gen'
    resourcePackageName = 'org.raml.test.res'
    modelPackageName = 'org.raml.test.model'
}
```
## Code Generation

Once the plugin has been applied and configured, execute the `raml-generate` task to generated the JAX-RS resources:

    ./gradlew ramltojaxrs
