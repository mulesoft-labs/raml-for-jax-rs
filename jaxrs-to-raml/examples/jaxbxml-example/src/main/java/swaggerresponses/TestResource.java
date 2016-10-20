package swaggerresponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

@Path("/customer")
public class TestResource {
	@Path("/{snt}")
	@Produces({ "application/json" })
	@GET
	@ApiOperation(value="Get customer matching specified SNT code.", response=TestObject.class)
	@ApiResponses(value={
	        @ApiResponse(code=200, message="OK", response=TestObject.class),
	        @ApiResponse(code=404, message="Not Found"),
	        @ApiResponse(code=500, message="Server Error")
	})
	
	public TestObject getCustomerBySntCode(@ApiParam(required=true, name="snt") @PathParam("snt") String snt) throws Throwable {
	    return (TestObject) null;
	}
}
