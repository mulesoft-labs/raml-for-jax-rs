package org.raml.jaxrs.generator.builders.extensions;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.raml.v2.api.model.v10.datamodel.NumberTypeDeclaration;

import javax.lang.model.element.Modifier;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * Created by Jean-Philippe Belanger on 12/12/16.
 * Just potential zeroes and ones
 */
public class Jsr303ExtensionTest {


    @Before
    public void annotations() {

        MockitoAnnotations.initMocks(this);
    }

    @Mock
    NumberTypeDeclaration number;

    @Test
    public void forInteger() throws Exception {

        when(number.minimum()).thenReturn(13.0);
        when(number.maximum()).thenReturn(17.0);
        when(number.required()).thenReturn(true);
        Jsr303Extension ext = new Jsr303Extension();
        FieldSpec.Builder builder = FieldSpec.builder(ClassName.get(Integer.class), "champ", Modifier.PUBLIC);

        ext.onFieldlementation(builder, number);

        assertTrue(builder.build().annotations.size() == 3);
        assertEquals(Min.class.getName(), builder.build().annotations.get(0).type.toString());
        assertEquals("13", builder.build().annotations.get(0).members.get("value").get(0).toString());
        assertEquals(Max.class.getName(), builder.build().annotations.get(1).type.toString());
        assertEquals("17", builder.build().annotations.get(1).members.get("value").get(0).toString());
        assertEquals(NotNull.class.getName(), builder.build().annotations.get(2).type.toString());
    }
}
