package org.raml.jaxrs.generator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 10/29/16.
 * Just potential zeroes and ones
 */
public class NamesTest {
    @Test
    public void buildResourceInterfaceName() throws Exception {

        assertEquals("Fun", Names.buildResourceInterfaceName("/fun"));
        assertEquals("Fun", Names.buildResourceInterfaceName("fun"));
        assertEquals("Root", Names.buildResourceInterfaceName(""));
        assertEquals("FunAllo", Names.buildResourceInterfaceName("fun allo"));
    }

}
