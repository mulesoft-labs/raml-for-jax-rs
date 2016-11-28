package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.types.RamlTypeGenerator;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.JSONTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class TypeHandler {
    private final CurrentBuild build;

    public TypeHandler(CurrentBuild build) {

        this.build = build;
    }

    public void handle(Api api, TypeDeclaration declaration) {

        handle(api, declaration, declaration.name(), false);
    }

    public RamlTypeGenerator handle(Api api, TypeDeclaration typeDeclaration, String typeName, boolean isInternal) {

        if (typeDeclaration instanceof ObjectTypeDeclaration) {

            return handleObjectType(api, (ObjectTypeDeclaration) typeDeclaration, typeName, isInternal);
        }

        if ( typeDeclaration instanceof JSONTypeDeclaration ) {

            JSONTypeDeclaration decl = (JSONTypeDeclaration) typeDeclaration;
            handleJsonSchema(api, decl);
        }

        if ( typeDeclaration instanceof XMLTypeDeclaration ) {

            XMLTypeDeclaration decl = (XMLTypeDeclaration) typeDeclaration;
            handleXmlSchema(api, decl);
        }

        return null;
    }

    private void handleXmlSchema(Api api, XMLTypeDeclaration decl) {

        build.createTypeFromXmlSchema(decl.name(), decl.schemaContent());
    }

    private void handleJsonSchema(Api api, JSONTypeDeclaration decl)  {

        build.createTypeFromJsonSchema(decl.name(), decl.schemaContent());
    }

    private RamlTypeGenerator handleObjectType(Api api, ObjectTypeDeclaration typeDeclaration, String typeName, boolean isInternal) {

        for (TypeDeclaration parentType: ModelFixer.parentTypes(api.types(), typeDeclaration)) {

            handle(api, parentType);
        }

        RamlTypeGenerator creator = build.createType(typeName,
                Lists.transform(ModelFixer.parentTypes(api.types(), typeDeclaration),
                typeToTypeName()), isInternal);

        for (TypeDeclaration declaration : typeDeclaration.properties()) {

            if (TypeUtils.isNewTypeDeclaration(api, declaration)) {

                RamlTypeGenerator internalGenerator = handle(api, declaration, declaration.name() + "_Type", true);
                creator.addInternalType(internalGenerator);
                creator.addProperty(declaration.type(), declaration.name(), true);
            } else {
                creator.addProperty(declaration.type(), declaration.name(), false);
            }
        }

        return creator;
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

    public void handlePrivateType(Api api, Resource resource, TypeDeclaration typeDeclaration) {

        handlePrivateType(api, resource.resourcePath() + "_" + typeDeclaration.name(), typeDeclaration);
    }

    public void handlePrivateType(Api api, Resource resource, Response response, TypeDeclaration typeDeclaration) {

        handlePrivateType(api, resource.resourcePath() + "_"  + response.code().value() + "_" + typeDeclaration.name(), typeDeclaration);
    }

    private void handlePrivateType(Api api, String name, TypeDeclaration typeDeclaration) {

        if ( TypeUtils.isNewTypeDeclaration(api, typeDeclaration)) {

            ObjectTypeDeclaration objectTypeDeclaration = (ObjectTypeDeclaration) typeDeclaration;

            for (TypeDeclaration parentType: ModelFixer.parentTypes(api.types(), typeDeclaration)) {

                handle(api, parentType);
            }

            RamlTypeGenerator creator = build.createPrivateType(name,
                    Lists.transform(ModelFixer.parentTypes(api.types(), typeDeclaration),
                            typeToTypeName()));

            for (TypeDeclaration declaration : objectTypeDeclaration.properties()) {

                if (TypeUtils.isNewTypeDeclaration(api, declaration)) {

                    RamlTypeGenerator internalGenerator = handle(api, declaration, declaration.name() + "_Type", true);
                    creator.addInternalType(internalGenerator);
                    creator.addProperty(declaration.type(), declaration.name(), true);
                } else {
                    creator.addProperty(declaration.type(), declaration.name(), false);
                }
            }
        }
    }
}
