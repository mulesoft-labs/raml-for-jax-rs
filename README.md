# JAXRS-to-RAML

##Introduction

The goal of JAXRS-to-RAML is to provide a full featured and easy to use tool to generate
a RAML API definition from an existing JAXRS-annotated Java code.

It's based on analyzing the existing code annotations.
(See "Supported annotations" to understand which annotations are being handled in the current version).


##Project Modules
So far, you can use JAXRS-to-RAML in the following ways:
- Eclipse/Mule Studio plugin: Perform the RAML generation from your IDE by selecting the package containing the source Java classes.
- Javac plugin: Perform the RAML generation from the Command Line.
- Runtime: Use JAXRS-to-RAML in your Java application to generate a RAML definition from some Java Code in Runtime.

##Key Supported Features:
- Accepting all basic action types, path annotations, path, query, and form parameters.
- Generating a resource tree based on the Jersey resources available in the source path.
- Inferring media types when possible.
- Including Javadoc documentation to resources, methods, and parameters descriptions.
- Statically determinable sub-resources (no overriding).
- Default values and validations for parameters (using javax.validation annotations).
- [XML/Json schemas and examples(stubs) generation with JAXB](https://github.com/mulesoft/jaxrs-to-raml/blob/master/jaxb.md)

### Supported Annotations
####JAXRS Annotations:
In JAXRS-to-RAML, these annotations have exactly the same semantical meaning as in JAXRS:
- Path.
- Consumes, Produces.
- QueryParam, FormParam, PathParam, HeaderParam.
- DELETE, GET, HEAD, OPTIONS, POST, PUT.
- DefaultValue.

**Note:** CookieParam and MatrixParam annotations are not supported in this version. A deep discussion
about how these should be represented in RAML must be held in order to have a good implementation. Feel free to [contribute
with ideas/opinions about it](https://github.com/mulesoft/jaxrs-to-raml/issues?labels=Cookie%26Matrix+params&milestone=&page=1&state=closed).
####Extended Annotations
The following annotations are not part of JAXRS specification itself. However, these are useful to describe RESTful APIs when working with
JAVA projects, and so, it was decided to add support for them.

#####Swagger Annotations:
If the project is using these swagger annotations, the tool is able to determine the possible response codes
and generate the proper documentation.
- ApiResponse
- ApiResponses

#####Validation Annotations:
These annotations are interpreted as RAML parameters constrains.
- NotNull.
- Pattern.
- Min.
- DecimalMin.
- Max.
- DecimalMax.

## Installation and Usage Guides
- [Installation instructions/Usage guide for Eclipse/Mule Studio](https://github.com/mulesoft/jaxrs-to-raml/blob/master/eclipseplugin.md)
- [Installation instructions/Usage guide for using as plugin to javac](https://github.com/mulesoft/jaxrs-to-raml/blob/master/javac.md)

##Examples
Packed with the project source code, you can find an ["examples" folder](https://github.com/mulesoft/jaxrs-to-raml/tree/master/examples).
This folder contains several Java Projects that you can use to try JAXRS-to-RAML.
The following snippets show one Java Class included on the examples, and the RAML result that the tool will generate for that class:

Java Class (CustomerResource.java):
```java
package shop.services;
import ...

@Path("/customers")
public interface CustomerResource
{
   @POST
   @Consumes("application/xml")
   Response createCustomer(Customer customer, @Context UriInfo uriInfo);

   @GET
   @Produces("application/xml")
   Customers getCustomers(@QueryParam("start") int start,
                          @QueryParam("size") @DefaultValue("2") int size,
                          @QueryParam("firstName") String firstName,
                          @QueryParam("lastName") String lastName,
                          @Context UriInfo uriInfo);

   @GET
   @Path("{id}")
   @Produces({"application/xml", "application/json"})
   Customer getCustomer(@PathParam("id") int id);
}

```

Generated RAML definition file:
```yaml
#%RAML 0.8
title: testJ2R
version: v1
baseUri: http://example.com
protocols: [ HTTP ]
schemas:
  - customer-jsonschema: |
        {
          "required" : true ,
          "$schema" : "http://json-schema.org/draft-03/schema" ,
          "type" : "object" ,
          "properties" : {
            "customer" : {
              "type" : "object" ,
              "required" : false ,
              "properties" : {
                "@id" : {
                  "type" : "string" ,
                  "required" : false
                }
              }
            }
          }
        }
  - customer: |
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">

          <xs:element name="customer" type="customer"/>

          <xs:complexType name="customer">
            <xs:sequence>
              <xs:element name="first-name" type="xs:string" minOccurs="0"/>
              <xs:element name="last-name" type="xs:string" minOccurs="0"/>
              <xs:element name="street" type="xs:string" minOccurs="0"/>
              <xs:element name="city" type="xs:string" minOccurs="0"/>
              <xs:element name="state" type="xs:string" minOccurs="0"/>
              <xs:element name="zip" type="xs:string" minOccurs="0"/>
              <xs:element name="country" type="xs:string" minOccurs="0"/>
            </xs:sequence>
            <xs:attribute name="id" type="xs:int" use="required"/>
          </xs:complexType>
        </xs:schema>
  - customers-jsonschema: |
        {
          "required" : true ,
          "$schema" : "http://json-schema.org/draft-03/schema" ,
          "type" : "object" ,
          "properties" : {
            "customers" : {
              "type" : "string" ,
              "required" : false
            }
          }
        }
  - customers: |
        <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
        <xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">

          <xs:element name="customer" type="customer"/>

          <xs:element name="customers" type="customers"/>

          <xs:element name="link" type="link"/>

          <xs:complexType name="customers">
            <xs:sequence>
              <xs:element ref="customer" minOccurs="0" maxOccurs="unbounded"/>
              <xs:element ref="link" minOccurs="0" maxOccurs="unbounded"/>
            </xs:sequence>
          </xs:complexType>

          <xs:complexType name="customer">
            <xs:sequence>
              <xs:element name="first-name" type="xs:string" minOccurs="0"/>
              <xs:element name="last-name" type="xs:string" minOccurs="0"/>
              <xs:element name="street" type="xs:string" minOccurs="0"/>
              <xs:element name="city" type="xs:string" minOccurs="0"/>
              <xs:element name="state" type="xs:string" minOccurs="0"/>
              <xs:element name="zip" type="xs:string" minOccurs="0"/>
              <xs:element name="country" type="xs:string" minOccurs="0"/>
            </xs:sequence>
            <xs:attribute name="id" type="xs:int" use="required"/>
          </xs:complexType>

          <xs:complexType name="link">
            <xs:sequence/>
            <xs:attribute name="href" type="xs:string"/>
            <xs:attribute name="rel" type="xs:string"/>
            <xs:attribute name="type" type="xs:string"/>
          </xs:complexType>
        </xs:schema>
/customers:
  get:
    queryParameters:
      start:
        type: integer
        required: true
      size:
        type: integer
        default: 2
      firstName:
      lastName:
    responses:
      200:
        body:
          application/xml:
            schema: customers
            example: |
              <?xml version="1.0" encoding="UTF-8"?>
              <customers></customers>
  post:
    body:
      application/xml:
        schema: customer
        example: |
          <?xml version="1.0" encoding="UTF-8"?>
          <customer id="0"></customer>
    responses:
      201:
  /{id}:
    uriParameters:
      id:
        type: integer
        required: true
    get:
      responses:
        200:
          body:
            application/xml:
              schema: customer
              example: |
                <?xml version="1.0" encoding="UTF-8"?>
                <customer id="0"></customer>
            application/json:
              schema: customer-jsonschema
              example: |
                {
                  "customer" : {
                    "@id" : "0"
                  }
                }

```

## Future Features
- Injection of request parameters to fields.
- Support for *XML examples*, *JSON examples*, and *JSON schemas* for the javac plugin.


