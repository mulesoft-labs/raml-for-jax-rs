package com.mulesoft.jaxrs.raml.annotation.tests;

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
 * FILE NAME: HelloWorldRest.java
 * 
 * 
 *
 *
 * Revision History: 
 * AUTHOR:              CHANGE:  
 * Auto generated       Initial Version  
 * 
 * 
 ******************************************************************************/

@Path("/world")
public interface HelloWorldRest {

	/**
	 * default method
	 */
	@Path("/")
	@GET
	@Produces({ VendorConstants.APP_DATATYPE_PREFIX + ".world+xml;version=1",
			VendorConstants.APP_DATATYPE_PREFIX + ".world+json;version=1" })
	public HelloWorldRest getRoot();

	@Path("countries")
	@POST
	
	@Consumes({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+xml;version=1;charset=UTF-8",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+json;version=1;charset=UTF-8" })
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+json;version=1;q=0.01" })
	public Country addCountry(Country country);

	@Path("countries")
	@GET
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.countries+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.countries+json;version=1;q=0.01" })
	public Countries getAllCountries();

	@Path("countries/{countryId}")
	@GET
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+json;version=1;q=0.01" })
	public Country getCountry(
			@PathParam("countryId") int countryId);

	@Path("countries/{countryId}")
	@PUT
	@Consumes({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+xml;version=1;charset=UTF-8",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+json;version=1;charset=UTF-8" })
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.country+json;version=1;q=0.01" })
	public Country updateCountry(Country country,
			
			@PathParam("countryId") int countryId);

	@Path("countries/{countryId}")
	@DELETE
	public void deleteCountry(@Context UriContext uriContext,
			@PathParam("countryId") int countryId);

	@Path("countries/{countryId}/states")
	@POST
	@Consumes({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state-ref+xml;version=1;charset=UTF-8",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state-ref+json;version=1;charset=UTF-8" })
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state-ref+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state-ref+json;version=1;q=0.01" })
	public State_REF addStateToCountry(State_REF state_ref,
			@Context UriContext uriContext,
			@PathParam("countryId") int countryId);

	@Path("countries/{countryId}/states")
	@GET
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.states-ref+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.states-ref+json;version=1;q=0.01" })
	public States_REF getStatesForCountry(
			@PathParam("countryId") int countryId,
			@Context UriContext uriContext);

	@Path("countries/{countryId}/states/{stateId}")
	@GET
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state-ref+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state-ref+json;version=1;q=0.01" })
	public State_REF getStateForCountry(
			@PathParam("countryId") int countryId,
			@PathParam("stateId") int stateId);

	@Path("countries/{countryId}/states/{stateId}")
	@DELETE
	public void deleteStateForCountry(@Context UriContext uriContext,
			@PathParam("countryId") int countryId,
			@PathParam("stateId") int stateId);

	@Path("run-countries-report")
	@POST
	@Produces({
			"application/vnd.net.juniper.space.job-management.task+xml;version=1;q=0.01",
			"application/vnd.net.juniper.space.job-management.task+json;version=1;q=0.01" })
	public Task runCountriesReport();

	@Path("states")
	@POST
	@Consumes({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+xml;version=1;charset=UTF-8",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+json;version=1;charset=UTF-8" })
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+json;version=1;q=0.01" })
	public State addState(State state, @Context UriContext uriContext);

	@Path("states")
	@GET
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.states+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.states+json;version=1;q=0.01" })
	public States getAllStates(
			@Context UriContext uriContext);

	@Path("states/{stateId}")
	@GET
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+json;version=1;q=0.01" })
	public State getState(
			@PathParam("stateId") int stateId);

	@Path("states/{stateId}")
	@PUT
	@Consumes({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+xml;version=1;charset=UTF-8",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+json;version=1;charset=UTF-8" })
	@Produces({
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+xml;version=1;q=0.01",
			VendorConstants.APP_DATATYPE_PREFIX
					+ ".world.state+json;version=1;q=0.01" })
	public State updateState(State state, @Context UriContext uriContext,
			@PathParam("stateId") int stateId);

	@Path("states/{stateId}")
	@DELETE
	public void deleteState(@Context UriContext uriContext,
			@PathParam("stateId") int stateId);
}
