At the moment we support following annotations:

JaxRS annotations:

 * Path
 * Consumes,@Produces,
 * QueryParam,FormParam,PathParam,HeaderParam
 * DELETE,GET,HEAD,OPTIONS,POST,PUT
 * DefaultValue

CookieParam and MatrixParam are not supported because it is not clear how they should be reflected in RAML

Swagger Annnotations:
 * ApiResponse
 * ApiResponses

Validation annotations:
 * NotNull
 * Pattern
 * Min
 * DecimalMin
 * Max
 * DecimalMax
