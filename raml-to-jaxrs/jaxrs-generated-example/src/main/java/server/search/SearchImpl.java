package server.search;

import example.helloworld.Search;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class SearchImpl implements Search {

    @Override
    public GetSearchResponse getSearch(String hello_dude, int fink, String entity) {
        return GetSearchResponse.respond200WithApplicationJson("hello");
    }
}
