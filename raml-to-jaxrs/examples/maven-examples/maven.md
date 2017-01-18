=======
![](http://raml.org/images/logo.png)
RAML for JAX-RS
===============

The maven plugin has many examples in the subprojects.
Some examples from the [types](raml-defined-example/pom.xml) example:
```xml
                <configuration>
                    <ramlFile>${project.build.resources[0].directory}/types_user_defined.raml</ramlFile>
                    <resourcePackage>example.resources</resourcePackage>
                    <modelPackage>example.model</modelPackage>
                    <resourcePackage>example.types</resourcePackage>
                    <supportPackage>example.support</supportPackage>
                </configuration>
```

The __ramlFile__ configuration parameter should point to your RAML file.

The __resourcePackage__ is the name of the resource package.  This parameter is mandatory, 
and serves as the default value for the modelPackage and supportPackage parameters, 
should they be undefined.  It determines the Java package in which the classes will appear.

The __modelPackage__ configuration parameter determines the package in which Java package the types 
will be generated.

The __supportPackage__ configuration parameter determines the package in which Java package the support classes 
(serializers, responses and such)  
will be generated.

There is an example of [json](simple-json-example/maven.md) generation. 
