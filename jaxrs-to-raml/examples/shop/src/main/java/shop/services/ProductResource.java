package shop.services;


import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import shop.domain.Product;
import shop.domain.Products;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/products")
public interface ProductResource
{
   @POST
   @Consumes("application/xml")
   Response createProduct(Product customer, @Context UriInfo uriInfo);

   @GET
   @Produces("application/xml")
   Products getProducts(@QueryParam("start") int start,
                        @QueryParam("size") @DefaultValue("2") int size,
                        @QueryParam("name") String name,
                        @Context UriInfo uriInfo);

   @GET
   @Path("{id}")
   @Produces("application/xml")
   Product getProduct(@PathParam("id") int id);
}