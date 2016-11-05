package org.raml.jaxrs.testproject;
//
//import jp.fun.Search;
//import jp.fun.SearchImpl;
//import org.eclipse.jetty.server.Server;
//import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
//import org.glassfish.jersey.server.ResourceConfig;
//import org.reflections.Reflections;
//
//import javax.ws.rs.Path;
//import javax.ws.rs.core.UriBuilder;
//import java.net.URI;
//import java.util.HashSet;
//import java.util.Set;

/**
 * Created by Jean-Philippe Belanger on 11/3/16.
 * Just potential zeroes and ones
 */
public class StartServer {

//    private static Set<Class<?>> findResourceClasses() {
//
//        Set<Class<?>> ret = new HashSet<Class<?>>();
//        Reflections reflections = new Reflections("jp.fun");
//        Set<Class<?>> interfaces = reflections.getTypesAnnotatedWith(Path.class);
//        for (Class anInterface : interfaces) {
//
//            Set<Class<Object>> subtypes = reflections.getSubTypesOf(anInterface);
//            ret.addAll(subtypes);
//        }
//
//        return ret;
//
//    }
//    public static void main(String[] args) throws Exception  {
//
//        Set<Class<?>> classes = findResourceClasses();
//        URI baseUri = UriBuilder.fromUri("http://localhost/").port(9998).build();
//        ResourceConfig config = new ResourceConfig(classes);
//        Server server = JettyHttpContainerFactory.createServer(baseUri, config);
//        server.start();
//    }
}
