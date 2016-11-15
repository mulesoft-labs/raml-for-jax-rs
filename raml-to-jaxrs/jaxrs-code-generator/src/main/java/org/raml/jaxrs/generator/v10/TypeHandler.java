package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.resources.ResourceBuilder;
import org.raml.jaxrs.generator.builders.types.TypeBuilder;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

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

            ObjectTypeDeclaration objectDeclaration = (ObjectTypeDeclaration) typeDeclaration;
            for (TypeDeclaration parentType: ModelFixer.parentTypes(api.types(), objectDeclaration)) {

                handle(api, parentType);
            }

            TypeBuilder creator = build.createType(objectDeclaration.name(), Lists.transform(ModelFixer.parentTypes(api.types(), objectDeclaration),
                    typeToTypeName()));

            for (TypeDeclaration declaration : objectDeclaration.properties()) {

                creator.addProperty(declaration.type(), declaration.name());
            }
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
