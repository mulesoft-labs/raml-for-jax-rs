package swaggerresponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

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
}
