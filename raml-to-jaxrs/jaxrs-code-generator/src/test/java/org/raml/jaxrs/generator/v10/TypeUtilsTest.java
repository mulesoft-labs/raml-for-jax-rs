package org.raml.jaxrs.generator.v10;

import org.junit.Assert;
import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.InputStreamReader;
import java.io.StringReader;

import static org.junit.Assert.*;

/**
 * Created by Jean-Philippe Belanger on 12/6/16.
 * Just potential zeroes and ones
 */
public class TypeUtilsTest {
    @Test
    public void shouldExtendingString() throws Exception {

        ObjectTypeDeclaration typeDeclaration = finder("extendString.raml").getDeclarations("ObjectOne");
        TypeDeclaration property = findProperty(typeDeclaration, "name");
        assertFalse(TypeUtils.shouldCreateNewClass(property, null));
    }

    @Test
    public void shouldExtendingObject() throws Exception {

        ObjectTypeDeclaration typeDeclaration = finder("extendObject.raml").getDeclarations("ObjectOne");
        TypeDeclaration property = findProperty(typeDeclaration, "name");
        assertTrue(TypeUtils.shouldCreateNewClass(property, null));
    }

    @Test
    public void shouldExtendingObjectWithProperties() throws Exception {

        ObjectTypeDeclaration typeDeclaration = finder("extendObjectWithProperties.raml").getDeclarations("ObjectOne");
        TypeDeclaration property = findProperty(typeDeclaration, "name");
        assertTrue(TypeUtils.shouldCreateNewClass(property, null));
    }

    @Test
    public void shouldExtendingAnother() throws Exception {

        TypeFinder finder = finder("extendingAnother.raml");
        ObjectTypeDeclaration object = finder.getDeclarations("ObjectOne");
        TypeDeclaration extending = findProperty(object, "name");
        ObjectTypeDeclaration extended = finder.getDeclarations(extending.type());
        assertFalse(TypeUtils.shouldCreateNewClass(extending, extended));
    }

    @Test
    public void shouldExtendingAnotherWithProperties() throws Exception {

        TypeFinder finder = finder("extendingAnotherWithProperties.raml");
        ObjectTypeDeclaration object = finder.getDeclarations("ObjectOne");
        TypeDeclaration extending = findProperty(object, "name");
        ObjectTypeDeclaration extended = finder.getDeclarations(extending.type());
        assertTrue(TypeUtils.shouldCreateNewClass(extending, extended));
    }

    @Test
    public void shouldExtendingAnotherMultipleInheritance() throws Exception {

        TypeFinder finder = finder("extendObjectMultipleIneritance.raml");
        ObjectTypeDeclaration object = finder.getDeclarations("ObjectOne");
        TypeDeclaration extending = findProperty(object, "name");
        ObjectTypeDeclaration extended = finder.getDeclarations(extending.type());
        assertTrue(TypeUtils.shouldCreateNewClass(extending, extended, finder.allTypes()));
    }

    @Test
    public void bigRaml() throws Exception {

        TypeFinder finder = finder("big.raml");
        ObjectTypeDeclaration object = finder.getDeclarations("RamlDataType");
        TypeDeclaration extending = findProperty(object, "NilValue");
        ObjectTypeDeclaration extended = finder.getDeclarations(extending.type());
        assertTrue(TypeUtils.shouldCreateNewClass(extending, extended, finder.allTypes()));
    }

    private TypeFinder finder(String raml) {
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(new InputStreamReader(this.getClass().getResourceAsStream(raml)), ".");
        if (ramlModelResult.hasErrors())
        {
            for (ValidationResult validationResult : ramlModelResult.getValidationResults())
            {
                System.out.println(validationResult.getMessage());
            }
            throw new AssertionError();
        }
        else
        {
            return new TypeFinder().findTypes(ramlModelResult.getApiV10());
        }
    }

    private TypeDeclaration findProperty(ObjectTypeDeclaration typeDeclaration, String stringProp) {

        for (TypeDeclaration declaration : typeDeclaration.properties()) {
            if ( declaration.name().equals(stringProp)) {
                return declaration;
            }
        }

        throw new AssertionError("no such prop " + stringProp);
    }

}
