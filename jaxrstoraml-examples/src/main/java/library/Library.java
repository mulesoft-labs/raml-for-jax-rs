package library;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("library")
public class Library
{
   private HashMap<String, Book> books = new HashMap<String, Book>();

   public Library()
   {
      books.put("596529260", new Book("Leonard Richardson", "596529260", "RESTful Web Services"));
      books.put("333333333", new Book("Bill Burke", "596529260", "EJB 3.0"));
   }

   @GET
   @Path("books/badger")
   @Produces("application/json")
   
   public BookListing getBooksBadger()
   {
      return getListing();
   }

   @GET
   @Path("books/mapped")
   @Produces("application/json")
   //@Mapped // mapped is the default format
   public BookListing getBooksMapped()
   {
      return getListing();
   }

   @GET
   @Path("books/badger.html")
   @Produces("text/html")
   public String getBooksBadgerText() throws Exception
   {
      @SuppressWarnings("unused")
	BookListing listing = getListing();
      //BadgerContext context = new BadgerContext(BookListing.class);
      StringWriter writer = new StringWriter();
      //Marshaller marshaller = context.createMarshaller();
      //marshaller.marshal(listing, writer);
      return writer.toString();
   }

   @SuppressWarnings("unused")
@GET
   @Path("books/mapped.html")
   @Produces("text/html")
   public String getBooksMappedText() throws Exception
   {
      BookListing listing = getListing();
      
      StringWriter writer = new StringWriter();
      //Marshaller marshaller = context.createMarshaller();
      //marshaller.marshal(listing, writer);
      return writer.toString();
   }




   private BookListing getListing()
   {
      ArrayList<Book> list = new ArrayList<Book>();
      list.addAll(books.values());
      return new BookListing(list);
   }

}
