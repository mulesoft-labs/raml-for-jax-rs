package org.raml.jaxrs.generator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.jaxrs.generator.ramltypes.GMethod;
import org.raml.jaxrs.generator.ramltypes.GParameter;
import org.raml.jaxrs.generator.ramltypes.GResource;
import org.raml.v2.api.model.v10.system.types.RelativeUriString;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
public class NamesTest {

    @Mock
    GResource resource;

    @Mock
    GMethod method;

    @Mock
    RelativeUriString url;

    @Mock
    GParameter uriParameter;

    @Before
    public void mocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void buildTypeName() throws Exception {


        assertEquals("Fun", Names.typeName("/fun"));
        assertEquals("Fun", Names.typeName("/fun"));
        assertEquals("CodeBytes", Names.typeName("//code//bytes"));
        assertEquals("Root", Names.typeName(""));
        assertEquals("FunAllo", Names.typeName("fun_allo"));
        assertEquals("FunAllo", Names.typeName("fun allo"));
        assertEquals("FunAllo", Names.typeName("funAllo"));
        assertEquals("FunAllo", Names.typeName("FunAllo"));

        assertEquals("FunAllo", Names.typeName("/FunAllo"));

        assertEquals("FunAllo", Names.typeName("Fun", "allo"));
        assertEquals("FunAllo", Names.typeName("fun", "_allo"));
        assertEquals("FunAllo", Names.typeName("fun", "allo"));

    }

    @Test
    public void buildVariableName() throws Exception {

        assertEquals("fun", Names.variableName("/fun"));
        assertEquals("fun", Names.variableName("/fun"));
        assertEquals("codeBytes", Names.variableName("//code//bytes"));
        assertEquals("root", Names.variableName(""));
        assertEquals("funAllo", Names.variableName("fun_allo"));
        assertEquals("funAllo", Names.variableName("fun allo"));
        assertEquals("funAllo", Names.variableName("funAllo"));
        assertEquals("funAllo", Names.variableName("FunAllo"));
        assertEquals("funAllo", Names.variableName("Fun", "allo"));
        assertEquals("funAllo", Names.variableName("fun", "_allo"));
        assertEquals("funAllo", Names.variableName("fun", "allo"));
    }

    @Test
    public void buildResponseClassname() throws Exception {

        when(method.resource()).thenReturn(resource);
        when(resource.resourcePath()).thenReturn("/songs");
        when(resource.uriParameters()).thenReturn(new ArrayList<GParameter>());
        when(method.method()).thenReturn("get");

        assertEquals("GetSongsResponse", Names.responseClassName(resource, method));
    }

    @Test
    public void buildResponseClassnameWithURIParam() throws Exception {

        when(method.resource()).thenReturn(resource);
        when(resource.resourcePath()).thenReturn("/songs/{songId}");
        when(uriParameter.name()).thenReturn("songId");
        when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter));
        when(method.method()).thenReturn("get");

        assertEquals("GetSongsBySongIdResponse", Names.responseClassName(resource, method));
    }

    @Test
    public void buildResponseClassnameWithTwoURIParam() throws Exception {

        when(method.resource()).thenReturn(resource);
        when(resource.resourcePath()).thenReturn("/songs/{songId}/{songId}");
        when(uriParameter.name()).thenReturn("songId");
        when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter, uriParameter));
        when(method.method()).thenReturn("get");

        assertEquals("GetSongsBySongIdAndSongIdResponse", Names.responseClassName(resource, method));
    }

    @Test
    public void buildResourceMethodClassname() throws Exception {

        when(method.resource()).thenReturn(resource);
        when(resource.resourcePath()).thenReturn("/songs");
        when(resource.uriParameters()).thenReturn(new ArrayList<GParameter>());
        when(method.method()).thenReturn("get");

        assertEquals("getSongs", Names.resourceMethodName(resource, method));
    }

    @Test
    public void buildResourceMethodNameWithURIParam() throws Exception {

        when(method.resource()).thenReturn(resource);
        when(resource.resourcePath()).thenReturn("/songs/{songId}");
        when(uriParameter.name()).thenReturn("songId");
        when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter));
        when(method.method()).thenReturn("get");

        assertEquals("getSongsBySongId", Names.resourceMethodName(resource, method));
    }

    @Test
    public void buildResourceMethodNameWithTwoURIParam() throws Exception {

        when(method.resource()).thenReturn(resource);
        when(resource.resourcePath()).thenReturn("/songs/{songId}/{songId}");
        when(uriParameter.name()).thenReturn("songId");
        when(resource.uriParameters()).thenReturn(Arrays.asList(uriParameter, uriParameter));
        when(method.method()).thenReturn("get");

        assertEquals("getSongsBySongIdAndSongId", Names.resourceMethodName(resource, method));
    }

}
