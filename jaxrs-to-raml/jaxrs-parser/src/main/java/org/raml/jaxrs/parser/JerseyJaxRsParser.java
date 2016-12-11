package org.raml.jaxrs.parser;

import org.raml.jaxrs.model.JaxRsApplication;
import org.raml.jaxrs.parser.analyzers.JerseyAnalyzer;
import org.raml.jaxrs.parser.gatherers.JerseyGatherer;
import org.raml.jaxrs.parser.util.ClassLoaderUtils;
import org.raml.utilities.format.Joiners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.Set;

import static java.lang.String.format;

class JerseyJaxRsParser implements JaxRsParser {

    private static final Logger logger = LoggerFactory.getLogger(JerseyJaxRsParser.class);

    private JerseyJaxRsParser() {
    }

    public static JerseyJaxRsParser create() {
        return new JerseyJaxRsParser();
    }

    @Override
    public JaxRsApplication parse(Path jaxRsResource) throws JaxRsParsingException {
        logger.info("parsing JaxRs resource: {}", jaxRsResource);

        Iterable<Class<?>> classes = getJaxRsClassesFor(jaxRsResource);

        return JerseyAnalyzer.create(classes).analyze();
    }

    private static Iterable<Class<?>> getJaxRsClassesFor(Path jaxRsResource) throws JaxRsParsingException {

        ClassLoader classLoader = null;
        try {
            classLoader = ClassLoaderUtils.classLoaderFor(jaxRsResource);
        } catch (MalformedURLException e) {
            throw new JaxRsParsingException(format("unable to create classloader from %s", jaxRsResource), e);
        }

        Set<Class<?>> classes = JerseyGatherer.builder()
                .forApplications(jaxRsResource)
                .withClassLoader(classLoader)
                .build()
                .jaxRsClasses();

        if (logger.isDebugEnabled()) {
            logger.debug("found JaxRs related classes: \n{}", Joiners.squareBracketsPerLineJoiner().join(classes));
        }

        return classes;
    }
}
