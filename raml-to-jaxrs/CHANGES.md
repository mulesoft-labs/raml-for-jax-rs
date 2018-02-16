# Breaking changes

Some things have changed from the 2.x track to the 3.x track.

- The handling of types have been moved to an external project [ramltopojo](https://https://github.com/mulesoft-labs/raml-java-tools/tree/master/raml-to-pojo). Please see that project.
- The plugin annotation RAML has changed.
- The plugin interfaces for both the handling of types and handling of resources have changed.
- The way to deliver plugins has changed to a more uniform format across projects.

## New Annotation RAMLs

The type annotation RAML is [here](https://github.com/mulesoft-labs/raml-java-tools/blob/release/1.0.0/raml-to-pojo/README.md)
```yaml
#%RAML 1.0 Library
annotationTypes:
    types:
        allowedTargets: [TypeDeclaration,API]
        properties:
            # used for jaxb and jsonschema modules.
            className?:
    resources:
        allowedTargets: [Resource, Method, Response, API]
        properties:
            plugins?: any[]
    methods:
        allowedTargets: [Method, API]
        properties:
            plugins?: any[]

    responseClasses:
        allowedTargets: [Method, API]
        properties:
            plugins?: any[]

    responses:
        allowedTargets: [Response, API]
        properties:
            plugins?: any[]
```

## New plugin interfaces

The interfaces have changed for the resource plugins, but not very much.  The combined interface is 
[here](jaxrs-code-generator/src/main/java/org/raml/jaxrs/generator/extension/resources/api/GlobalResourceExtension.java).
There used to be two methods (onCreate* and onFinish*), not there is only one method, called on finish.  The rest is pretty similar semantically.

The type interfaces have changed significantly, and are described in the raml-to-pojo project.  The mthod names and 
arguments have changed, and the plugins have been split up into types (object, union, enumeration arrays and reference types).
These allow for the user writing plugins in a simpler manner (only adjusting types that they want to adjust:  why have to think of unions if you are not using them)

## Plugin delivery

Plugins used to be delivered as plain Java classes that were referenced in the annotations.  However, this is overly verbose and 
ties implementation name to the specified RAML.  Plugins are now defined through a properties file in the jar META-INF. See the features project
for a specific [example](examples/maven-examples/features/feature-plugins) (this project specifies a plugin for types) or 
the [default](jaxrs-code-generator/src/main/resources/META-INF/ramltojaxrs-plugin.properties) plugins.

# Non breaking change

The maven plugin tag representing extensions can now include general plugins, if it doesn't take arguments. 
```xml
<generateTypesWith>
    <value>jackson</value>
    <value>jsr303</value>
    <value>boxWhenNotRequired</value>
    <value>myplugin.cleanup</value>
</generateTypesWith>
```

This configuration actually activates *core.jackson*, *core.jsr303*, *core.boxWhenNotRequired* and *myplugin.cleanup*, on both resource generation and type generation.