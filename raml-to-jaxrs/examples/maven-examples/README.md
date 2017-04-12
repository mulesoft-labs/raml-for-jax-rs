## Maven plugin examples
The examples included in this folder all come with a server that can be started using `mvn exec:java`.

Here's an example configuration:
```xml
<configuration>
    <ramlFile>${project.build.resources[0].directory}/types_user_defined.raml</ramlFile>
    <resourcePackage>example.resources</resourcePackage>
    <modelPackage>example.model</modelPackage>
    <resourcePackage>example.types</resourcePackage>
    <supportPackage>example.support</supportPackage>
</configuration>
```

The `ramlFile` configuration parameter should point to your RAML file.

The `resourcePackage` is the name of the resource package. This parameter is mandatory,
and serves as the default value for the modelPackage and supportPackage parameters,
should they be undefined.  It determines the Java package in which the classes will appear.

The `modelPackage` configuration parameter determines the package in which Java package the types
will be generated.

The `supportPackage` configuration parameter determines the package in which Java package the support classes
(serializers, responses and such)
will be generated.
