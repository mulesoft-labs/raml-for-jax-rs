package org.raml.jaxrs.codegen.core.ext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.raml.model.Raml;
import org.raml.model.Resource;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ResourceInterfaceNameWithVersionTest {
    public static final String BASE_URI = "http://api.example.org";
    private ResourceInterfaceNameWithVersion resourceInterfaceNameWithVersion = new ResourceInterfaceNameWithVersion();
    private Resource resource;
    private Raml raml;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init() {
        resource = new Resource();
        raml = new Raml();
        resourceInterfaceNameWithVersion.setRaml(raml);
        resource.setParentUri("");
    }

    @Test
    public void testResourceNameWithVersion() throws Exception {
        raml.setBaseUri(BASE_URI);
        raml.setVersion("v1");
        resource.setRelativeUri("/test");
        String resourceInterfaceName = resourceInterfaceNameWithVersion.buildResourceInterfaceName(resource);
        assertThat("Resource name does not have a version", resourceInterfaceName, is("TestResourceV1"));
    }

    @Test
    public void testResourceNameWithoutVersion() throws Exception {
        raml.setBaseUri(BASE_URI);
        raml.setVersion(null);
        resource.setRelativeUri("/test");
        expectedException.expect(IllegalArgumentException.class);
        resourceInterfaceNameWithVersion.buildResourceInterfaceName(resource);
    }
}