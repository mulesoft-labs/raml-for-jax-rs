![](http://raml.org/images/logo.png)
##Examples

This folder contains several Java Projects that you can use to try RAML for JAX-RS (JAX-RS->RAML generation).
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
