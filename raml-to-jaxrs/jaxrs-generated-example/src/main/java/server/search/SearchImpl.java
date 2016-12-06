package server.search;

import example.helloworld.Search;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class SearchImpl implements Search {


    @Override
    public GetSearchResponse getSearch(String helloDude, int fink, String entity) {
        return GetSearchResponse.respond200WithApplicationJson("hello");

    }

    @Override
    public GetSearchByIdResponse getSearchById(String id, String helloDude, int fink, String entity) {
        return GetSearchByIdResponse.respond200WithApplicationJson("hello_again");
    }

}
