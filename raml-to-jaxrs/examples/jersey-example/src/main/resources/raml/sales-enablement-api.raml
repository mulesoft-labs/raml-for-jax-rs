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
