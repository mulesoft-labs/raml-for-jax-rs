![](http://raml.org/images/logo.png)

# RAML to JAX-RS codegen - Gradle Plug-in

This plugin provides RAML code generation support to a Gradl project.

## Usage

To use it, simply include the required plugin dependency via `buildscript {}` and 'apply' the plugin:

```groovy
apply plugin: 'java'
apply plugin: 'raml'

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

raml {
    basePackageName = 'example.resources'
}
```

## Configuration

The Gradle plugin supports the following configuration options, defined via the `raml` closure:

|Property|Description|Default Value|Required|
|:-------|:----------|:------------|--------|
|**basePackageName**|The base Java package name used for the generated JAX-RS resource files.|**Yes**|
|**jaxrsVersion**|The JAX-RS target version|*1.1*|No|
|**jsonMapper**|The JSON mapper target version|*JACKSON1*|No|
|**outputDirectory**|The output directory for the generated JAX-RS resource source files.|*$project.buildDir/generated-sources/raml-jaxrs*|No|
|**sourceDirectory**|The path to the directory containing source .raml and .yaml files.|*$project.rootDir/src/main/raml*|No|
|**sourcePaths**|The set of source .raml and .yaml files in addition to those found in the source directory.||No|
|**useJsr303Annotations**|Determines whether or not JSR-303 annotations will be used in the generated source|*false*|No|

For example:

```groovy
raml {
    basePackageName = 'example.resources'
    jaxrsVersion = '2.0'
    jsonMapper = 'JACKSON2'
    outputDirectory = 'src/main/gen-src'
    sourceDirectory = 'src/main/resources/other'
    useJsr303Annotations = true
}
```

or

```groovy
raml {
    basePackageName = 'example.resources'
    jaxrsVersion = '2.0'
    jsonMapper = 'JACKSON2'
    outputDirectory = 'src/main/gen-src'
    sourcePaths = files('src/main/resources/other/my.raml', 'src/main/resources/base.yaml')
    useJsr303Annotations = true
}
```

## Code Generation

Once the plugin has been applied and configured, execute the `raml-generate` task to generated the JAX-RS resources:

    ./gradlew raml-generate
