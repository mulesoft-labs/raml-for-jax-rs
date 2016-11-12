package org.raml.model.impl;

import org.raml.model.RamlApi;

import static com.google.common.base.Preconditions.checkNotNull;

public class RamlApiImpl implements RamlApi {

    private final String title;
    private final String version;
    private final String baseUri;

    private RamlApiImpl(String title, String version, String baseUri) {
        this.title = title;
        this.version = version;
        this.baseUri = baseUri;
    }

    public static RamlApiImpl create(String title, String version, String baseUri) {
        checkNotNull(title);
        checkNotNull(version);
        checkNotNull(baseUri);

        return new RamlApiImpl(title, version, baseUri);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getBaseUri() {
        return null;
    }
}
