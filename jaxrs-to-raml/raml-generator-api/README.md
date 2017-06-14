## The RAML generator API
It is possible to influence the generation of the RAML file in the JAX-RS to RAML scenario in two ways: first you can
change how types and properties from these types are detected. Secondly, you can add examples to the RAML from the Java
code.

## The `@RamlGenerator` annotation
Types that need to be made into RAML types need to be annotated with the `@RamlGenerator` annotation.  As an argument to
that annotation, you must pass an instance of the [TypeHandler](src/main/java/org/raml/jaxrs/plugins/TypeHandler.java) class.
This class breaks the Java type into a RAML type and its properties.  There are three existing type handlers:  a
[SimpleJacksonTypes](src/main/java/org/raml/jaxrs/handlers/SimpleJacksonTypes.java) handler that looks at simple jackson
annotations to figure out which are the properties that are part of the RAML type.

Similarly, the [SimpleJaxbTypes](src/main/java/org/raml/jaxrs/handlers/SimpleJaxbTypes.java) will look at JAXB annotation
to try to figure out what are the RAML types.  Both these implementations are relatively simple in their approach:  we
handle properties and don't handle inheritance (yet).

The final handler doesn't really care about annotations: the [BeanLikeTypes](src/main/java/org/raml/jaxrs/handlers/BeanLikeTypes.java).
This type treats the class as a JavaBean, so it doesn't look at any form of annotation.  Inheritance is not handler here either (yet).

Should you want to influence RAML type generation, look at the existing types a go from there.  We aim of improve the interface
and documentation of these classes soon.

## RAML examples
RAML examples are a feature in RAML 1.0 that allow the spec writer to provide example values elements of the specification.
Normally, examples in RAML are parsed and verified against their containing spec:  if you give an example  in a spec, it should
be correct:  you can't add or omit properties, or use the wrong types.  You can prevent this parsing of examples by specifying
that the examples are not strict by specifying that the examples are not `strict` (see raml spec ?)

In our case, the examples need to be specified in Java.  Type properties can be annotated with the
[Example](src/main/java/org/raml/jaxrs/common/Example.java) annotation in which you provide an example value.  You can have
multiple examples per type, specified with the [ExampleCases](src/main/java/org/raml/jaxrs/common/ExampleCases.java) annotation, where
you specify examples for use cases.  `jaxrstoraml` will the aggregate the examples values with regards to each use case name.

There are examples of this in the
[example](../jaxrs-to-raml-examples/jaxrs-to-raml-maven-examples/jaxrs-to-raml-raml-examples) project.
