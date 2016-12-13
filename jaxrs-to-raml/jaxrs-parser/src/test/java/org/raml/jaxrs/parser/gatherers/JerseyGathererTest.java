package org.raml.jaxrs.parser.gatherers;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

public class JerseyGathererTest {

    @Mock
    ResourceConfig resourceConfig;

    private JerseyGatherer jerseyGatherer;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jerseyGatherer = new JerseyGatherer(resourceConfig);
    }


    @Test
    public void testConstructor() {
        assertSame(resourceConfig, jerseyGatherer.getResourceConfig());
    }
}