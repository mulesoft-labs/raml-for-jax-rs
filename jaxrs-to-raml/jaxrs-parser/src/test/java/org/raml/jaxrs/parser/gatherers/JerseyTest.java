package org.raml.jaxrs.parser.gatherers;

import com.google.common.collect.Lists;

import org.glassfish.jersey.server.model.Resource;
import org.junit.Test;

import java.util.List;

public class JerseyTest {

    @Test
    public void test() {
        ReflectionsGatherer gatherer = ReflectionsGatherer.forPackage("");

        List<Resource> resources = Lists.newArrayList();
        for (Class<?> clazz : gatherer.getJaxRsClasses()) {
            resources.add(Resource.from(clazz));
        }

        System.out.println(resources);
    }
}
