At the moment we support following annotations:

JaxRS annotations:

 * Path
 * Consumes,@Produces,
 * QueryParam,FormParam,PathParam,HeaderParam
 * DELETE,GET,HEAD,OPTIONS,POST,PUT
 * DefaultValue
 
This annotations have exactly same semantic meaning as in JaxRs 

CookieParam and MatrixParam are not supported because it is not clear how they should be reflected in RAML

Swagger Annnotations:
 * ApiResponse
 * ApiResponses

When this annotations exist we are able to determine possible response codes and documentation from them.

Validation annotations:
 * NotNull
 * Pattern
 * Min
 * DecimalMin
 * Max
 * DecimalMax

When this annotations exist we are using them to add restrictions on RAML parameters 
