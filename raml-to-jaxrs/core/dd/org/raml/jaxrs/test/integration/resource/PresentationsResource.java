
package org.raml.jaxrs.test.integration.resource;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.raml.jaxrs.codegen.core.ext.TestAnnotation;
import org.raml.jaxrs.test.integration.model.Presentation_;
import org.raml.jaxrs.test.integration.model.Presentations;
import org.raml.jaxrs.test.integration.resource.support.patch;

@Path("presentations")
@TestAnnotation
public interface PresentationsResource {


    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param pages
     *     The number of pages to return
     * @param start
     *     The first page to return
     * @param title
     *     Filter by title
     */
    @GET
    @Produces({
        "application/json"
    })
    @TestAnnotation
    PresentationsResource.GetPresentationsResponse getPresentations(
        @HeaderParam("Authorization")
        String authorization,
        @QueryParam("title")
        String title,
        @QueryParam("start")
        BigDecimal start,
        @QueryParam("pages")
        BigDecimal pages)
        throws Exception
    ;

    /**
     * 
     * @param entity
     *     
     */
    @POST
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    @TestAnnotation
    PresentationsResource.PostPresentationsResponse postPresentations(Presentation_ entity)
        throws Exception
    ;

    /**
     * 
     * @param presentationId
     *     
     */
    @GET
    @Path("{presentationId}")
    @Produces({
        "application/json"
    })
    @TestAnnotation
    PresentationsResource.GetPresentationsByPresentationIdResponse getPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId)
        throws Exception
    ;

    /**
     * 
     * @param presentationId
     *     
     * @param entity
     *     
     */
    @PUT
    @Path("{presentationId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    @TestAnnotation
    PresentationsResource.PutPresentationsByPresentationIdResponse putPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId, Presentation_ entity)
        throws Exception
    ;

    /**
     * 
     * @param presentationId
     *     
     * @param entity
     *     
     */
    @patch
    @Path("{presentationId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    @TestAnnotation
    PresentationsResource.PatchPresentationsByPresentationIdResponse patchPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId, Presentation_ entity)
        throws Exception
    ;

    /**
     * 
     * @param presentationId
     *     
     */
    @DELETE
    @Path("{presentationId}")
    @TestAnnotation
    PresentationsResource.DeletePresentationsByPresentationIdResponse deletePresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId)
        throws Exception
    ;

    public class DeletePresentationsByPresentationIdResponse
        extends org.raml.jaxrs.test.integration.resource.support.ResponseWrapper
    {


        private DeletePresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         */
        public static PresentationsResource.DeletePresentationsByPresentationIdResponse withOK() {
            Response.ResponseBuilder responseBuilder = Response.status(200);
            return new PresentationsResource.DeletePresentationsByPresentationIdResponse(responseBuilder.build());
        }

		@Override
		public StatusType getStatusInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T readEntity(Class<T> entityType) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T readEntity(GenericType<T> entityType) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public MediaType getMediaType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Locale getLanguage() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getLength() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Set<String> getAllowedMethods() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, NewCookie> getCookies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public EntityTag getEntityTag() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getLastModified() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URI getLocation() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Set<Link> getLinks() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasLink(String relation) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Link getLink(String relation) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Builder getLinkBuilder(String relation) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MultivaluedMap<String, String> getStringHeaders() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getHeaderString(String name) {
			// TODO Auto-generated method stub
			return null;
		}

    }

    public class GetPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.test.integration.resource.support.ResponseWrapper
    {


        private GetPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static PresentationsResource.GetPresentationsByPresentationIdResponse withJsonOK(Presentation_ entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new PresentationsResource.GetPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class GetPresentationsResponse
        extends org.raml.jaxrs.test.integration.resource.support.ResponseWrapper
    {


        private GetPresentationsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Unauthorized
         * 
         */
        public static PresentationsResource.GetPresentationsResponse withUnauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new PresentationsResource.GetPresentationsResponse(responseBuilder.build());
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static PresentationsResource.GetPresentationsResponse withJsonOK(Presentations entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new PresentationsResource.GetPresentationsResponse(responseBuilder.build());
        }

    }

    public class PatchPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.test.integration.resource.support.ResponseWrapper
    {


        private PatchPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static PresentationsResource.PatchPresentationsByPresentationIdResponse withJsonOK(Presentation_ entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new PresentationsResource.PatchPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class PostPresentationsResponse
        extends org.raml.jaxrs.test.integration.resource.support.ResponseWrapper
    {


        private PostPresentationsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Created
         * 
         * @param entity
         *     
         */
        public static PresentationsResource.PostPresentationsResponse withJsonCreated(Presentation_ entity) {
            Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new PresentationsResource.PostPresentationsResponse(responseBuilder.build());
        }

    }

    public class PutPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.test.integration.resource.support.ResponseWrapper
    {


        private PutPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static PresentationsResource.PutPresentationsByPresentationIdResponse withJsonOK(Presentation_ entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new PresentationsResource.PutPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

}
