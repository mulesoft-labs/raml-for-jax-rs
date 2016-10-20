
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
import org.raml.jaxrs.example.model.Presentations;
import org.raml.jaxrs.example.model.Product;
import org.raml.jaxrs.example.support.PATCH;

@Path("products")
public interface Products {


    /**
     * 
     * @param region
     *     Filter by region
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
    Products.GetProductsResponse getProducts(
        @HeaderParam("Authorization")
        String authorization,
        @QueryParam("region")
        String region,
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
    Products.PostProductsResponse postProducts(
        @HeaderParam("Authorization")
        String authorization, Product entity);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param productId
     *     
     */
    @GET
    @Path("{productId}")
    @Produces({
        "application/json"
    })
    Products.GetProductsByProductIdResponse getProductsByProductId(
        @PathParam("productId")
        String productId,
        @HeaderParam("Authorization")
        String authorization);

    /**
     * 
     * @param entity
     *     
     * @param authorization
     *     The auth token for this request
     * @param productId
     *     
     */
    @PUT
    @Path("{productId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Products.PutProductsByProductIdResponse putProductsByProductId(
        @PathParam("productId")
        String productId,
        @HeaderParam("Authorization")
        String authorization, Product entity);

    /**
     * 
     * @param entity
     *     
     * @param authorization
     *     The auth token for this request
     * @param productId
     *     
     */
    @PATCH
    @Path("{productId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Products.PatchProductsByProductIdResponse patchProductsByProductId(
        @PathParam("productId")
        String productId,
        @HeaderParam("Authorization")
        String authorization, Product entity);

    /**
     * 
     * @param authorization
     *     The auth token for this request
     * @param productId
     *     
     */
    @DELETE
    @Path("{productId}")
    void deleteProductsByProductId(
        @PathParam("productId")
        String productId,
        @HeaderParam("Authorization")
        String authorization);

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
    @Path("{productId}/presentations")
    @Produces({
        "application/json"
    })
    Products.GetProductsByProductIdPresentationsResponse getProductsByProductIdPresentations(
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
    @Path("{productId}/presentations")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Products.PostProductsByProductIdPresentationsResponse postProductsByProductIdPresentations(
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
    @Path("{productId}/presentations/{presentationId}")
    @Produces({
        "application/json"
    })
    Products.GetProductsByProductIdPresentationsByPresentationIdResponse getProductsByProductIdPresentationsByPresentationId(
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
    @Path("{productId}/presentations/{presentationId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Products.PutProductsByProductIdPresentationsByPresentationIdResponse putProductsByProductIdPresentationsByPresentationId(
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
    @Path("{productId}/presentations/{presentationId}")
    @Consumes("application/json")
    @Produces({
        "application/json"
    })
    Products.PatchProductsByProductIdPresentationsByPresentationIdResponse patchProductsByProductIdPresentationsByPresentationId(
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
    @Path("{productId}/presentations/{presentationId}")
    void deleteProductsByProductIdPresentationsByPresentationId(
        @PathParam("presentationId")
        String presentationId,
        @HeaderParam("Authorization")
        String authorization);

    public class GetProductsByProductIdPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private GetProductsByProductIdPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Products.GetProductsByProductIdPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.GetProductsByProductIdPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.GetProductsByProductIdPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.GetProductsByProductIdPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class GetProductsByProductIdPresentationsResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private GetProductsByProductIdPresentationsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.GetProductsByProductIdPresentationsResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.GetProductsByProductIdPresentationsResponse(responseBuilder.build());
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Products.GetProductsByProductIdPresentationsResponse jsonOK(Presentations entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.GetProductsByProductIdPresentationsResponse(responseBuilder.build());
        }

    }

    public class GetProductsByProductIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private GetProductsByProductIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Products.GetProductsByProductIdResponse jsonOK(Product entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.GetProductsByProductIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.GetProductsByProductIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.GetProductsByProductIdResponse(responseBuilder.build());
        }

    }

    public class GetProductsResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private GetProductsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.GetProductsResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.GetProductsResponse(responseBuilder.build());
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Products.GetProductsResponse jsonOK(org.raml.jaxrs.example.model.Products entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.GetProductsResponse(responseBuilder.build());
        }

    }

    public class PatchProductsByProductIdPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PatchProductsByProductIdPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Products.PatchProductsByProductIdPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.PatchProductsByProductIdPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.PatchProductsByProductIdPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.PatchProductsByProductIdPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class PatchProductsByProductIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PatchProductsByProductIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Products.PatchProductsByProductIdResponse jsonOK(Product entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.PatchProductsByProductIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.PatchProductsByProductIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.PatchProductsByProductIdResponse(responseBuilder.build());
        }

    }

    public class PostProductsByProductIdPresentationsResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PostProductsByProductIdPresentationsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Created
         * 
         * @param entity
         *     
         */
        public static Products.PostProductsByProductIdPresentationsResponse jsonCreated(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.PostProductsByProductIdPresentationsResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.PostProductsByProductIdPresentationsResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.PostProductsByProductIdPresentationsResponse(responseBuilder.build());
        }

    }

    public class PostProductsResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PostProductsResponse(Response delegate) {
            super(delegate);
        }

        /**
         * Created
         * 
         * @param entity
         *     
         */
        public static Products.PostProductsResponse jsonCreated(Product entity) {
            Response.ResponseBuilder responseBuilder = Response.status(201).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.PostProductsResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.PostProductsResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.PostProductsResponse(responseBuilder.build());
        }

    }

    public class PutProductsByProductIdPresentationsByPresentationIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PutProductsByProductIdPresentationsByPresentationIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Products.PutProductsByProductIdPresentationsByPresentationIdResponse jsonOK(Presentation entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.PutProductsByProductIdPresentationsByPresentationIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.PutProductsByProductIdPresentationsByPresentationIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.PutProductsByProductIdPresentationsByPresentationIdResponse(responseBuilder.build());
        }

    }

    public class PutProductsByProductIdResponse
        extends org.raml.jaxrs.example.support.ResponseWrapper
    {


        private PutProductsByProductIdResponse(Response delegate) {
            super(delegate);
        }

        /**
         * OK
         * 
         * @param entity
         *     
         */
        public static Products.PutProductsByProductIdResponse jsonOK(Product entity) {
            Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
            responseBuilder.entity(entity);
            return new Products.PutProductsByProductIdResponse(responseBuilder.build());
        }

        /**
         * Unauthorized
         * 
         */
        public static Products.PutProductsByProductIdResponse unauthorized() {
            Response.ResponseBuilder responseBuilder = Response.status(401);
            return new Products.PutProductsByProductIdResponse(responseBuilder.build());
        }

    }

}
