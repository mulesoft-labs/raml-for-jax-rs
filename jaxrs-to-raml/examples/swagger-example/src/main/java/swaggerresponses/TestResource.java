package swaggerresponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/")
public class TestResource {

	
	@Path("ww")
	@Produces("text/html")
	@ApiResponse(code=201,message="Test message")
	@GET
	String getMe(){
		return "";		
	}
	
	@Path("ww1")
	@Produces("text/html")
	@ApiResponses(value={@ApiResponse(code=201,message="Test message"),@ApiResponse(code=401,message="Not found")})
	@GET
	String getMe1(){
		return "";		
	}
	@Path("/{snt}")
	@Produces({ "application/json" })
	@GET
	@ApiOperation(value="Get customer matching specified SNT code.", response=Customer.class)
	@ApiResponses(value={
	        @ApiResponse(code=404, message="Not Found"),
	        @ApiResponse(code=500, message="Server Error")
	})
	public Customer getCustomerBySntCode(@ApiParam(required=true, name="snt") @PathParam("snt") String snt, @Context Object req) throws Throwable {
		return null;		
	}
}
