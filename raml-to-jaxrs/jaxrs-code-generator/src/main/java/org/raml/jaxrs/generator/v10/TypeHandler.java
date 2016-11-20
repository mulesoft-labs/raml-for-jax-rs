package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.types.RamlTypeGenerator;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

import javax.annotation.Nullable;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class TypeHandler {
    private final CurrentBuild build;

    public TypeHandler(CurrentBuild build) {

        this.build = build;
    }

    public void handle(Api api, TypeDeclaration typeDeclaration) {


        if (typeDeclaration instanceof ObjectTypeDeclaration) {

            handleObjectType(api, (ObjectTypeDeclaration) typeDeclaration);
            return;
        }

        if ( typeDeclaration instanceof JSONTypeDeclaration ) {

            JSONTypeDeclaration decl = (JSONTypeDeclaration) typeDeclaration;
            handleJsonSchema(api, decl);
        }

        if ( typeDeclaration instanceof XMLTypeDeclaration ) {

            XMLTypeDeclaration decl = (XMLTypeDeclaration) typeDeclaration;
            handleXmlSchema(api, decl);
        }

    }

    private void handleXmlSchema(Api api, XMLTypeDeclaration decl) {

        build.createTypeFromXmlSchema(decl.name(), decl.schemaContent());
    }

    private void handleJsonSchema(Api api, JSONTypeDeclaration decl)  {

        build.createTypeFromJsonSchema(decl.name(), decl.schemaContent());
    }

    private void handleObjectType(Api api, ObjectTypeDeclaration typeDeclaration) {
        ObjectTypeDeclaration objectDeclaration = typeDeclaration;
        for (TypeDeclaration parentType: ModelFixer.parentTypes(api.types(), objectDeclaration)) {

            handle(api, parentType);
        }

        RamlTypeGenerator creator = build.createType(objectDeclaration.name(), Lists
                .transform(ModelFixer.parentTypes(api.types(), objectDeclaration),
                typeToTypeName()));

        for (TypeDeclaration declaration : objectDeclaration.properties()) {

            creator.addProperty(declaration.type(), declaration.name());
        }
    }

    private Function<TypeDeclaration, String> typeToTypeName() {
        return new Function<TypeDeclaration, String>() {
            @Nullable
            @Override
            public String apply(@Nullable TypeDeclaration input) {
                return input.name();
            }
        };
    }
}
