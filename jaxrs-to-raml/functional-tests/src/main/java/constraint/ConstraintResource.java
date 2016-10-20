package constraint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/")
public class ConstraintResource {

	@Path("busUnit/retail/{unitID}/status/{enabled : (?i)(true)|(false)}")
	@GET
	public Response setZZZZ(@PathParam("unitID") int unitId,@PathParam("enabled")int enabled) {
	    return null;
	}
}
