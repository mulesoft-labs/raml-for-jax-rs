/*
 * Copyright 2013-2015 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.jaxrs.example.impl;

import javax.ws.rs.WebApplicationException;

import org.raml.jaxrs.example.model.Presentation;
import org.raml.jaxrs.example.model.Presentations;
import org.raml.jaxrs.example.resource.PresentationsResource;

/**
 * <p>PresentationResource class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class PresentationResourceImpl implements PresentationsResource
{
    /** {@inheritDoc} */
    public GetPresentationsResponse getPresentations(final String authorization,
                                                     final String title,
                                                     final Integer start,
                                                     final Integer pages)
    {
        if (!"s3cr3t".equals(authorization))
        {
            return GetPresentationsResponse.withUnauthorized();
        }
        	
        final Presentations presentations = new Presentations();
        presentations.setSize(1);
        Presentation presentation = new Presentation();
        presentation.setId("fake-id");
        presentation.setTitle(title);
		presentations.getItems().add(presentation);
        return GetPresentationsResponse.withJsonOK(presentations);
    }

    /** {@inheritDoc} */
    public PostPresentationsResponse postPresentations(final String authorization, final Presentation entity)
    {
        if (!"s3cr3t".equals(authorization))
        {
            throw new WebApplicationException(401);
        }

        entity.setId("fake-new-id");

        return PostPresentationsResponse.withJsonCreated(entity);
    }

    /** {@inheritDoc} */
    public GetPresentationsByPresentationIdResponse getPresentationsByPresentationId(final String presentationId,
                                                                                     final String authorization)
    {
        if (!"s3cr3t".equals(authorization))
        {
        	throw new WebApplicationException(401);
        }

        Presentation presentation = new Presentation();
        presentation.setId(presentationId);
        presentation.setTitle("Title of " + presentationId);
		return GetPresentationsByPresentationIdResponse.withJsonOK(presentation);
    }

    /** {@inheritDoc} */
    public PutPresentationsByPresentationIdResponse putPresentationsByPresentationId(final String presentationId,
                                                                                     final String authorization,
                                                                                     final Presentation entity)
    {
        // TODO implement me!
        return null;
    }

    /** {@inheritDoc} */
    public PatchPresentationsByPresentationIdResponse patchPresentationsByPresentationId(final String presentationId,
                                                                                         final String authorization,
                                                                                         final Presentation entity)
    {
        // TODO implement me!
        return null;
    }

    /** {@inheritDoc} */
    public DeletePresentationsByPresentationIdResponse deletePresentationsByPresentationId(final String presentationId, final String authorization)
    {
        // TODO implement me!
    	return null;
    }

	@Override
	public GetPresentationsResponse getPresentations(String authorization, String title, int start, int pages)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PostPresentationsResponse postPresentations(Presentation entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GetPresentationsByPresentationIdResponse getPresentationsByPresentationId(String presentationId)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PutPresentationsByPresentationIdResponse putPresentationsByPresentationId(String presentationId,
			Presentation entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PatchPresentationsByPresentationIdResponse patchPresentationsByPresentationId(String presentationId,
			Presentation entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DeletePresentationsByPresentationIdResponse deletePresentationsByPresentationId(String presentationId)
			throws Exception {
		// TODO Auto7-generated method stub
		return null;
	}
    
}
