package shop.services;


import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import shop.domain.Link;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class StoreResourceBean implements StoreResource
{
   public Response head(UriInfo uriInfo)
   {
      UriBuilder absolute = uriInfo.getBaseUriBuilder();
      String customerUrl = absolute.clone().path("customers").build().toString();
      String orderUrl = absolute.clone().path("orders").build().toString();
      String productUrl = absolute.clone().path("products").build().toString();

      Response.ResponseBuilder builder = Response.ok();
      builder.header("Link", new Link("customers", customerUrl, "application/xml"));
      builder.header("Link", new Link("orders", orderUrl, "application/xml"));
      builder.header("Link", new Link("products", productUrl, "application/xml"));
      return builder.build();
   }
}
