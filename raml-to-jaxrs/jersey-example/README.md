![](http://raml.org/images/logo.png)

# RAML to JAX-RS converter - Jersey Example

This project demonstrates the usage of the RAML-JAX-RS Maven plug-in in a Jersey 2 API project.

## Pre-requisites

- JDK6 or better
- Maven 3

## Running and Testing

Run `org.raml.jaxrs.example.Main` for example with:

    mvn exec:java

Then:

    curl -H "Authorization: s3cr3t" http://localhost:8181/presentations?title=Some%20title

    curl -H "Authorization: s3cr3t" -H "Content-Type: application/json" -d '{"title":"New presentation"}' http://localhost:8181/presentations

So In this example following RAML file (you may see full output in 'example-output' folder):
```yaml
#%RAML 0.8
---
title: "Muse: Mule Sales Enablement API"
version: v1
schemas:
  - presentation: |
      {  "$schema": "http://json-schema.org/draft-03/schema",
         "type": "object",
         "description": "A single product Presentation",
         "properties": {
           "id":  { "type": "string", "required": true },
           "title":  { "type": "string", "required": true },
           "description":  { "type": "string" },
           "fileUrl":  { "type": "string", "required": true },
           "productId":  { "type": "string", "required": true }
         }
      }
  - presentations: |
      {  "$schema": "http://json-schema.org/draft-03/schema",
         "type": "object",
         "description": "A collection of product Presentations",
         "properties": {
           "size":  { "type": "integer", "required": true },
           "presentations":  {
              "type": "array",
              "items": { "$ref": "presentation" }
           }
         }
      }
  - product: |
      {  "$schema": "http://json-schema.org/draft-03/schema",
         "type": "object",
         "description": "A single Product",
         "properties": {
           "id":  { "type": "string", "required": true },
           "name":  { "type": "string", "required": true },
           "description":  { "type": "string" },
           "imageUrl":  { "type": "string" },
           "region": { "type": "string", "required": true }
         }
      }
  - products: |
      {  "$schema": "http://json-schema.org/draft-03/schema",
         "type": "object",
         "description": "A collection of Products",
         "properties": {
           "size":  { "type": "integer", "required": true },
           "products":  {
              "type": "array",
              "items": { "$ref": "product" }
           }
         }
      }
resourceTypes:
  - base:
      get?:
        responses: &standardResponses
          200:
            description: OK
      put?:
        responses: *standardResponses
      patch?:
        responses: *standardResponses
      post?:
          responses:
            201:
              description: Created
      delete?:
        responses: *standardResponses
  - collection:
      type: base
      get:
        is: [ paged ]
      post:
  - typedCollection:
      type: collection
      get:
        responses:
          200:
            body:
              application/json:
                schema: <<collectionSchema>>
      post:
        body:
          application/json:
            schema: <<schema>>
        responses:
          201:
            body:
              application/json:
                schema: <<schema>>
  - member:
      type: base
      get:
      put:
      patch:
      delete:
  - typedMember:
      type: member
      get:
        responses:
          200:
            body:
              application/json:
                schema: <<schema>>
      put:
        body:
          application/json:
            schema: <<schema>>
        responses:
          200:
            body:
              application/json:
                schema: <<schema>>
      patch:
        body:
          application/json:
            schema: <<schema>>
        responses:
          200:
            body:
              application/json:
                schema: <<schema>>
      delete:
traits:
  - paged:
      displayName: paged
      queryParameters:
        start:
          displayName: start
          description: The first page to return
          type: integer
        pages:
          displayName: pages
          description: The number of pages to return
          type: integer
  - secured:
      displayName: secured
      headers:
        Authorization:
          description: The auth token for this request
      responses:
        401:
          description: Unauthorized
/presentations: &presentations
  type: { typedCollection: { schema: presentation, collectionSchema: presentations } }
  is: [ secured ]
  get:
    queryParameters:
      title:
        type: string
        displayName: title
        description: Filter by title
  /{presentationId}:
    type: { typedMember: { schema: presentation } }
    is: [ secured ]
/products:
  type: { typedCollection: { schema: product, collectionSchema: products } }
  is: [ secured ]
  get:
    queryParameters:
      region:
        type: string
        displayName: region
        description: Filter by region
  /{productId}:
    type: { typedMember: { schema: product } }
    is: [ secured ]
    /presentations: *presentations

```
Produces the set of Java Class included on the examples which will look like:
as well as required model and support classes

```java

package org.raml.jaxrs.example.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.raml.jaxrs.example.model.Presentation;
import org.raml.jaxrs.example.support.PATCH;

@Path("presentations")
public interface Presentations {


    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param pages
     *     The number of pages to return
     * @param start
     *     The first page to return
     * @param title
     *     Filter by title
     */
    @GET
    @Produces({
        "application/json"
    })
    Presentations.GetPresentationsResponse getPresentations(
        @HeaderParam("Authorization")
        String authorization,
        @QueryParam("title")
        String title,
        @QueryParam("start")
        Long start,
        @QueryParam("pages")
        Long pages);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param entity
     *     
     */
    @POST
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Presentations.PostPresentationsResponse postPresentations(
        @HeaderParam("Authorization")
        String authorization, Presentation entity);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param presentationId
     *     
     */
    @GET
    @Path("{presentationId}")
    @Produces({
        "application/json"
    })
    Presentations.GetPresentationsByPresentationIdResponse getPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param presentationId
     *     
     * @param entity
     *     
     */
    @PUT
    @Path("{presentationId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Presentations.PutPresentationsByPresentationIdResponse putPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization, Presentation entity);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param presentationId
     *     
     * @param entity
     *     
     */
    @PATCH
    @Path("{presentationId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Presentations.PatchPresentationsByPresentationIdResponse patchPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization, Presentation entity);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param presentationId
     *     
     */
    @DELETE
    @Path("{presentationId}")
    void deletePresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization);

    public class GetPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private GetPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Presentations.GetPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.GetPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.GetPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.GetPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class GetPresentationsResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private GetPresentationsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.GetPresentationsResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.GetPresentationsResponse(responseBuilder.build());
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Presentations.GetPresentationsResponse jsonOK(org.raml.jaxrs.example.model.Presentations entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.GetPresentationsResponse(responseBuilder.build());
        }

    }

    public class PatchPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PatchPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Presentations.PatchPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.PatchPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.PatchPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.PatchPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class PostPresentationsResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PostPresentationsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Created
         * 
         * @param entity
         *     
         */
        public static Presentations.PostPresentationsResponse jsonCreated(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.PostPresentationsResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.PostPresentationsResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.PostPresentationsResponse(responseBuilder.build());
        }

    }

    public class PutPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PutPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Presentations.PutPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.PutPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.PutPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.PutPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

}

```
