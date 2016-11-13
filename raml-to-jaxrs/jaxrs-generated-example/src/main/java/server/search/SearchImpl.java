package server.search;

import example.helloworld.Search;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class SearchImpl implements Search {

    @Override
    public GetByHelloDudeAndFinkResponse getByHelloDudeAndFink(String helloDude, int fink, String entity) {

        return GetByHelloDudeAndFinkResponse.respond200("hello_world");
    }

    @Override
    public GetIdByHelloDudeAndFinkResponse getIdByHelloDudeAndFink(String helloDude, int fink, String id,
            String entity) {

        return GetIdByHelloDudeAndFinkResponse.respond200(id);
    }
}
