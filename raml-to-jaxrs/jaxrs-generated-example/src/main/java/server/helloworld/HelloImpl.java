package server.helloworld;

import example.helloworld.Hello;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class HelloImpl implements Hello {

    @Override
    public GetByHelloDudeAndFinkResponse getByHelloDudeAndFink(String helloDude, int fink, String entity) {

        return Hello.GetByHelloDudeAndFinkResponse.respond200("hello_world");
    }

    @Override
    public GetIdByHelloDudeAndFinkResponse getIdByHelloDudeAndFink(String helloDude, int fink, String id,
            String entity) {

        return GetIdByHelloDudeAndFinkResponse.respond200(id);
    }
}
