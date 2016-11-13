package org.raml.jaxrs.parser;

import com.google.common.base.Joiner;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.analyzers.JerseyAnalyzer;
import org.raml.jaxrs.parser.gatherers.JerseyGatherer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Set;

public class JaxRsParser {

    private static final Logger logger = LoggerFactory.getLogger(JaxRsParser.class);

    private JaxRsParser() {
    }

    public static JaxRsParser create() {
        return new JaxRsParser();
    }

    public JaxRsApplication parse(Path jaxRsResource) throws JaxRsParsingException {
        logger.info("parsing JaxRs resource: {}", jaxRsResource);

        Iterable<Class<?>> classes = getJaxRsClassesFor(jaxRsResource);

        return JerseyAnalyzer.create(classes).analyze();
    }

    private static Iterable<Class<?>> getJaxRsClassesFor(Path jaxRsResource) {

        Set<Class<?>> classes = JerseyGatherer.forApplication(jaxRsResource).jaxRsClasses();

        if (logger.isDebugEnabled()) {
            String classesString = classes.isEmpty() ? "[]" : String.format("\n[\n  %s\n]", Joiner.on(",\n  ").join(classes));
            logger.debug("found JaxRs related classes: {}", classesString);
        }

        return classes;
    }
}
