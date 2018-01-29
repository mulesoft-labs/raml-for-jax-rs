/**
 * Created. There, you have it.
 */
@RamlGenerators({
        @RamlGeneratorForClass(
                forClass = UUID.class,
                generator = @RamlGenerator(parser = BeanLikeClassParser.class, plugins = {@RamlGeneratorPlugin(plugin = "core.changeType")})
        )
    }
)
package org.raml.jaxrs.examples.resources;

import org.raml.jaxrs.common.RamlGenerator;
import org.raml.jaxrs.common.RamlGeneratorForClass;
import org.raml.jaxrs.common.RamlGeneratorPlugin;
import org.raml.jaxrs.common.RamlGenerators;
import org.raml.jaxrs.handlers.BeanLikeClassParser;

import java.util.UUID;