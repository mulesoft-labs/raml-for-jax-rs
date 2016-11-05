package org.raml.jaxrs.parser.providers;

import com.google.common.collect.ImmutableList;
import com.google.common.net.MediaType;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import javax.activation.DataSource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBElement;
import javax.xml.transform.Source;

public enum  JavaTypeDefaultProvider implements EnityProvider {
    BYTE_ARRAY(byte[].class, MediaType.ANY_TYPE),
    STRING(String.class, MediaType.ANY_TEXT_TYPE),
    INPUT_STREAM(InputStream.class, MediaType.ANY_TYPE),
    READER(Reader.class, MediaType.ANY_TYPE),
    FILE(File.class, MediaType.ANY_TYPE),
    DATA_SOURCE(DataSource.class, MediaType.ANY_TYPE),
    SOURCE(Source.class, MediaType.XML_UTF_8, MediaType.APPLICATION_XML_UTF_8, MediaType.create("application", "*+xml")),
    JAXB_ELEMENT(JAXBElement.class, MediaType.XML_UTF_8, MediaType.APPLICATION_XML_UTF_8, MediaType.create("application", "*+xml")),
    MULTI_VALUED_MAP(MultivaluedMap.class, MediaType.FORM_DATA),
    STREAMING_OUTPUT(StreamingOutput.class, MediaType.ANY_TYPE);

    private final Class<?> clazz;
    private final ImmutableList<MediaType> mediaType;

    JavaTypeDefaultProvider(Class<?> clazz, MediaType first, MediaType... theRest) {
        this(clazz, ImmutableList.<MediaType>builder().add(first).add(theRest).build());
    }

    JavaTypeDefaultProvider(Class<?> clazz, ImmutableList<MediaType> mediaTypes) {
        this.clazz = clazz;
        this.mediaType = mediaTypes;
    }



    @Override
    public Iterable<MediaType> getMediaTypes() {
        return mediaType;
    }
}
