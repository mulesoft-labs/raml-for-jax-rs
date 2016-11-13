package org.raml.model.impl;

import com.google.common.collect.ImmutableList;

import org.raml.model.RamlApi;
import org.raml.model.Resource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class RamlApiImpl implements RamlApi {

    private final String title;
    private final String version;
    private final String baseUri;
    private final ImmutableList<Resource> resources;

    private RamlApiImpl(String title, String version, String baseUri, ImmutableList<Resource> resources) {
        this.title = title;
        this.version = version;
        this.baseUri = baseUri;
        this.resources = resources;
    }

    public static RamlApiImpl create(String title, String version, String baseUri, Iterable<Resource> resources) {
        checkNotNull(title);
        checkNotNull(version);
        checkNotNull(baseUri);
        checkNotNull(resources);

        return new RamlApiImpl(title, version, baseUri, ImmutableList.copyOf(resources));
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getBaseUri() {
        return this.baseUri;
    }

    @Override
    public List<Resource> getResources() {
        return resources;
    }
}
