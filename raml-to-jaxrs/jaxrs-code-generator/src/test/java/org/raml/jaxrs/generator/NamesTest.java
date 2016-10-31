package org.raml.jaxrs.generator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
public class NamesTest {
    @Test
    public void buildTypeName() throws Exception {

        assertEquals("Fun", Names.buildTypeName("/fun"));
        assertEquals("Fun", Names.buildTypeName("fun"));
        assertEquals("Root", Names.buildTypeName(""));
        assertEquals("FunAllo", Names.buildTypeName("fun_allo"));
        assertEquals("FunAllo", Names.buildTypeName("fun allo"));
    }

    @Test
    public void buildVariableName() throws Exception {

        assertEquals("fun", Names.buildVariableName("/fun"));
        assertEquals("fun", Names.buildVariableName("fun"));
        assertEquals("funAllo", Names.buildVariableName("fun allo"));
    }

}
