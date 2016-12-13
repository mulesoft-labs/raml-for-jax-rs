package org.raml.jaxrs.parser.gatherers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import org.glassfish.jersey.server.ResourceConfig;
import org.raml.jaxrs.parser.util.ClassLoaderUtils;
import org.raml.utilities.builder.NonNullableField;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.raml.utilities.builder.Preconditions.checkSet;
import static org.raml.utilities.builder.Preconditions.checkUnset;

/**
 * A {@link JaxRsClassesGatherer} implementation that leverages Jersey
 * code.
 */
public class JerseyGatherer implements JaxRsClassesGatherer {

    private final ResourceConfig resourceConfig;

    @VisibleForTesting
    JerseyGatherer(ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    private static JerseyGatherer create(ResourceConfig resourceConfig) {
        checkNotNull(resourceConfig);

        return new JerseyGatherer(resourceConfig);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Set<Class<?>> jaxRsClasses() {
        return resourceConfig.getClasses();
    }

    @VisibleForTesting
    ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public static class Builder {
        private NonNullableField<List<Path>> applications = NonNullableField.unset();
        private NonNullableField<ClassLoader> classLoader = NonNullableField.unset();

        private Builder() {
        }

        public Builder forApplications(Path first, Path... theRest) {
            checkUnset(applications, "applications");

            ImmutableList<Path> paths = ImmutableList.<Path>builder().add(first).add(theRest).build();

            this.applications = NonNullableField.<List<Path>>of(paths);
            return this;
        }

        public Builder withClassLoader(ClassLoader classLoader) {
            checkUnset(this.classLoader, "class loader");

            this.classLoader = NonNullableField.of(classLoader);

            return this;
        }

        public JerseyGatherer build() {
            checkSet(applications, "applications");

            ResourceConfig resourceConfig = new ResourceConfig();
            resourceConfig.files(true, FluentIterable.from(this.applications.get()).transform(
                    new Function<Path, String>() {
                        @Nullable
                        @Override
                        public String apply(@Nullable Path path) {
                            return path.toString();
                        }
                    }
            ).toArray(String.class));

            if (classLoader.isSet()) {
                resourceConfig.setClassLoader(classLoader.get());
            }

            return create(resourceConfig);
        }


    }
}
