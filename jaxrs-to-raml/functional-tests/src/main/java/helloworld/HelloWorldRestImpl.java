package helloworld;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;


/*******************************************************************************
 * FILE NAME: HelloWorldRestImpl.java
 * 
 *
 *
 * Revision History: 
 * AUTHOR:              CHANGE:  
 * Auto generated       Initial Version  
 * 
 * 
 ******************************************************************************/

public class HelloWorldRestImpl  implements HelloWorldRest{

	@Override
	@Path("/")
	@GET
	@Produces({ "hello.world+xml;version=1", "hello.world+json;version=1" })
	public HelloWorldRest getRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("countries")
	@POST
	@Consumes({ "hello.world.country+xml;version=1;charset=UTF-8",
			"hello.world.country+json;version=1;charset=UTF-8" })
	@Produces({ "hello.world.country+xml;version=1;q=0.01",
			"hello.world.country+json;version=1;q=0.01" })
	public Country addCountry(Country country, @Context UriContext uriContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("countries")
	@GET
	@Produces({ "hello.world.countries+xml;version=1;q=0.01",
			"hello.world.countries+json;version=1;q=0.01" })
	public Countries getAllCountries(@Context UriContext uriContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("countries/{countryId}")
	@GET
	@Produces({ "hello.world.country+xml;version=1;q=0.01",
			"hello.world.country+json;version=1;q=0.01" })
	public Country getCountry(@PathParam("countryId") int countryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("countries/{countryId}")
	@PUT
	@Consumes({ "hello.world.country+xml;version=1;charset=UTF-8",
			"hello.world.country+json;version=1;charset=UTF-8" })
	@Produces({ "hello.world.country+xml;version=1;q=0.01",
			"hello.world.country+json;version=1;q=0.01" })
	public Country updateCountry(Country country,
			@Context UriContext uriContext,
			@PathParam("countryId") int countryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("countries/{countryId}")
	@DELETE
	public void deleteCountry(@Context UriContext uriContext,
			@PathParam("countryId") int countryId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Path("countries/{countryId}/states")
	@POST
	@Consumes({ "hello.world.state-ref+xml;version=1;charset=UTF-8",
			"hello.world.state-ref+json;version=1;charset=UTF-8" })
	@Produces({ "hello.world.state-ref+xml;version=1;q=0.01",
			"hello.world.state-ref+json;version=1;q=0.01" })
	public State_REF addStateToCountry(State_REF state_ref,
			@Context UriContext uriContext,
			@PathParam("countryId") int countryId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("countries/{countryId}/states")
	@GET
	@Produces({ "hello.world.states-ref+xml;version=1;q=0.01",
			"hello.world.states-ref+json;version=1;q=0.01" })
	public States_REF getStatesForCountry(
			@PathParam("countryId") int countryId,
			@Context UriContext uriContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("countries/{countryId}/states/{stateId}")
	@GET
	@Produces({ "hello.world.state-ref+xml;version=1;q=0.01",
			"hello.world.state-ref+json;version=1;q=0.01" })
	public State_REF getStateForCountry(@PathParam("countryId") int countryId,
			@PathParam("stateId") int stateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("countries/{countryId}/states/{stateId}")
	@DELETE
	public void deleteStateForCountry(@Context UriContext uriContext,
			@PathParam("countryId") int countryId,
			@PathParam("stateId") int stateId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Path("run-countries-report")
	@POST
	@Produces({
			"application/vnd.net.juniper.space.job-management.task+xml;version=1;q=0.01",
			"application/vnd.net.juniper.space.job-management.task+json;version=1;q=0.01" })
	public Task runCountriesReport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("states")
	@POST
	@Consumes({ "hello.world.state+xml;version=1;charset=UTF-8",
			"hello.world.state+json;version=1;charset=UTF-8" })
	@Produces({ "hello.world.state+xml;version=1;q=0.01",
			"hello.world.state+json;version=1;q=0.01" })
	public State addState(State state, @Context UriContext uriContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("states")
	@GET
	@Produces({ "hello.world.states+xml;version=1;q=0.01",
			"hello.world.states+json;version=1;q=0.01" })
	public States getAllStates(@Context UriContext uriContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("states/{stateId}")
	@GET
	@Produces({ "hello.world.state+xml;version=1;q=0.01",
			"hello.world.state+json;version=1;q=0.01" })
	public State getState(@PathParam("stateId") int stateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("states/{stateId}")
	@PUT
	@Consumes({ "hello.world.state+xml;version=1;charset=UTF-8",
			"hello.world.state+json;version=1;charset=UTF-8" })
	@Produces({ "hello.world.state+xml;version=1;q=0.01",
			"hello.world.state+json;version=1;q=0.01" })
	public State updateState(State state, @Context UriContext uriContext,
			@PathParam("stateId") int stateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Path("states/{stateId}")
	@DELETE
	public void deleteState(@Context UriContext uriContext,
			@PathParam("stateId") int stateId) {
		// TODO Auto-generated method stub
		
	}
}
