package server.search;

import example.helloworld.Search;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class SearchImpl implements Search {

    @Override
    public GetByIdResponse getById(String id, String helloDude, int fink, String entity) {
        return GetByIdResponse.respond200WithApplicationJson("hello_again");
    }

    @Override
    public GetSearchResponse getSearch(String hello_dude, int fink, String entity) {
        return GetSearchResponse.respond200WithApplicationJson("hello");
    }
}
