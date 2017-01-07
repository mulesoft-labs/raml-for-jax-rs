package server.raml08;

import example.simpleraml08.ApiRequest;
import example.simpleraml08.Jobs;
import example.simpleraml08.JobsPostApplicationJson;

/**
 * Created by Jean-Philippe Belanger on 11/11/16.
 * Just potential zeroes and ones
 */
public class JobsImpl implements Jobs {

    @Override
    public PostJobsResponse postJobs(ApiRequest entity) {
        return null;
    }

    @Override
    public PostJobsResponse postJobs(JobsPostApplicationJson entity) {
        return null;
    }
}
