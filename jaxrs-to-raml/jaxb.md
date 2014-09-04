![](http://raml.org/images/logo.png)
#JAXB and Schema Generation Support

At the moment schema and example generation is based on JAXB annotations. So in order to get the schemas and examples generated,
your DTOs (Data Transfer Objects) must be annotated with @XMLRootElement used by JAXB.

#XML Example Generation:

XML examples generation is based on the previously generated XML schema and is designed to be the minimal valid XML inferred from the schema.
Having said that, if you needed a real life example you should use it as a draft and enrich it with real world values.

*JSON Schema* and *Example* are currently generated from XML example
