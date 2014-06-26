# JAXB and Schema generation support

At the moment schema and example generation is based on JAXB annotations. So in order to get schemas and examples generated
your DTO (Data transfer objects) should be annotated with @XMLRootElement annotation used by JAXB.

XML Example generation:

Xml example is generated basing on previously generated XML schema and is designed to be a minimal workable xml derived from schema. So if you would like to get a real life example you should use it as a draft and enrich it with real world values

JSON Schema and example, JSON schema and example are currently generated from XML example
