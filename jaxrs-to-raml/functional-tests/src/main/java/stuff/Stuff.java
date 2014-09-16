package stuff;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/admin/mule-versions")
@Produces(MediaType.APPLICATION_JSON)
public class Stuff {

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("{version}")
    public Response get(@PathParam("version") String version) {
        return Response.ok("stuff").build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(MuleVersionExtDTO versionExtDTO) {
        return Response.created(uriInfo.getBaseUriBuilder().path("abc").build()).build();
    }

    @PUT
    @Path("{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("version") String version, MuleVersionExtDTO versionExtDTO) {
            return Response.ok().build();
    }

    
    @PUT
    @Path("{version}/w")
    @Consumes(MediaType.APPLICATION_JSON)
    public MuleVersionExtDTO update2(@PathParam("version") String version) {
           return null;
    }
}
