package org.raml.jaxrs.example.impl;

import org.raml.jaxrs.example.model.AlertableAdmin;
import org.raml.jaxrs.example.model.Org;
import org.raml.jaxrs.example.resource.OrgsOrgIdResource;

public class OrgsIdResourceImpl implements OrgsOrgIdResource{

	@Override
	public GetOrgsByOrgIdResponse getOrgsByOrgId(String orgId) throws Exception {
		
		final Org entity = new Org();
		
		final AlertableAdmin value2 = new AlertableAdmin();
		value2.setFirstname("Ada");
		value2.setLastname("The dog");
		entity.setOnCall(value2);
		return GetOrgsByOrgIdResponse.withJsonOK(entity);
	}

}
