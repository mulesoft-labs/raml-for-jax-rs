
package org.raml.jaxrs.example.resource;

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
import javax.ws.rs.core.Response;
import org.raml.jaxrs.example.model.Presentation;
import org.raml.jaxrs.example.support.PATCH;

@Path("presentations")
public interface Presentations {


    /**
     * 
     * @param title
     *     Filter by title
     * @param start
     *     The first page to return
     * @param pages
     *     The number of pages to return
     * @param authorization
     *     The auth token for this request
     */
    @GET
    @Produces({
        "application/json"
    })
    Presentations.GetPresentationsResponse getPresentations(
        @HeaderParam("Authorization")
        String authorization,
        @QueryParam("title")
        String title,
        @QueryParam("start")
        Long start,
        @QueryParam("pages")
        Long pages);

    /**
     * 
     * @param entity
     *     
     * @param authorization
     *     The auth token for this request
     */
    @POST
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Presentations.PostPresentationsResponse postPresentations(
        @HeaderParam("Authorization")
        String authorization, Presentation entity);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param presentationId
     *     
     */
    @GET
    @Path("{presentationId}")
    @Produces({
        "application/json"
    })
    Presentations.GetPresentationsByPresentationIdResponse getPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization);

    /**
     * 
     * @param entity
     *     
     * @param authorization
     *     The auth token for this request
     * @param presentationId
     *     
     */
    @PUT
    @Path("{presentationId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Presentations.PutPresentationsByPresentationIdResponse putPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization, Presentation entity);

    /**
     * 
     * @param entity
     *     
     * @param authorization
     *     The auth token for this request
     * @param presentationId
     *     
     */
    @PATCH
    @Path("{presentationId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Presentations.PatchPresentationsByPresentationIdResponse patchPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization, Presentation entity);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param presentationId
     *     
     */
    @DELETE
    @Path("{presentationId}")
    void deletePresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization);

    public class GetPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
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
        public static Presentations.GetPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.GetPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.GetPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.GetPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class GetPresentationsResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private GetPresentationsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.GetPresentationsResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.GetPresentationsResponse(responseBuilder.build());
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Presentations.GetPresentationsResponse jsonOK(org.raml.jaxrs.example.model.Presentations entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.GetPresentationsResponse(responseBuilder.build());
        }

    }

    public class PatchPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
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
        public static Presentations.PatchPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.PatchPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.PatchPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.PatchPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class PostPresentationsResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
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
        public static Presentations.PostPresentationsResponse jsonCreated(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.PostPresentationsResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.PostPresentationsResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.PostPresentationsResponse(responseBuilder.build());
        }

    }

    public class PutPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
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
        public static Presentations.PutPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Presentations.PutPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Presentations.PutPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Presentations.PutPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

}
