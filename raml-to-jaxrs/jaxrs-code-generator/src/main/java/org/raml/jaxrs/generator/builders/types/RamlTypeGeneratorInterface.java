package org.raml.jaxrs.generator.builders.types;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.GType;
import org.raml.jaxrs.generator.GeneratorType;
import org.raml.jaxrs.generator.Names;
import org.raml.jaxrs.generator.builders.AbstractTypeGenerator;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jean-Philippe Belanger on 11/13/16.
 * Just potential zeroes and ones
 */
public class RamlTypeGeneratorInterface extends AbstractTypeGenerator<TypeSpec.Builder> implements RamlTypeGenerator {
    private final CurrentBuild build;
    private final List<GType> parentTypes;
    private final ClassName interf;

    private Map<String, PropertyInfo> propertyInfos = new HashMap<>();
    private Map<String, JavaPoetTypeGenerator> internalTypes = new HashMap<>();
    private final V10GType typeDeclaration;


    public RamlTypeGeneratorInterface(CurrentBuild currentBuild, ClassName interf, List<GType> parentTypes,
            List<PropertyInfo> properties, Map<String, JavaPoetTypeGenerator> internalTypes, V10GType typeDeclaration) {

        this.build = currentBuild;
        this.interf = interf;
        this.parentTypes = parentTypes;
        this.internalTypes = internalTypes;
        this.typeDeclaration = typeDeclaration;
        for (PropertyInfo property : properties) {
            propertyInfos.put(property.getName(), property);
        }
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> into, TYPE type) throws IOException {

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) typeDeclaration.implementation();

        final TypeSpec.Builder typeSpec = TypeSpec
                .interfaceBuilder(interf)
                .addModifiers(Modifier.PUBLIC);

        build.withTypeListeners().onTypeDeclaration(build, typeSpec, object);

        for (JavaPoetTypeGenerator internalType : internalTypes.values()) {

            internalType.output(new CodeContainer<TypeSpec.Builder>() {
                @Override
                public void into(TypeSpec.Builder g) throws IOException {

                    g.addModifiers(Modifier.STATIC);
                    typeSpec.addType(g.build());
                }
            }, type);
        }


        List<GeneratorType> propsFromParents = new ArrayList<>();
        for (GType parentType : parentTypes) {

            if ( parentType.name().equals("object") ) {

                continue;
            }
            GeneratorType builder = build.getDeclaredType(parentType.name());

            propsFromParents.add(builder);
            typeSpec.addSuperinterface(build.getJavaType(builder.getDeclaredType()));
        }

        for (PropertyInfo propertyInfo : propertyInfos.values()) {

                final MethodSpec.Builder getSpec = MethodSpec
                        .methodBuilder("get" + Names.typeName(propertyInfo.getName()))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                getSpec.returns(propertyInfo.resolve(build, internalTypes));
                build.withTypeListeners().onGetterMethodDeclaration(build,
                        getSpec, (TypeDeclaration) propertyInfo.getType().implementation());
                typeSpec.addMethod(getSpec.build());

                if ( ! propertyInfo.getName().equals(object.discriminator()) ) {
                    MethodSpec.Builder setSpec = MethodSpec
                            .methodBuilder("set" + Names.typeName(propertyInfo.getName()))
                            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);
                    ParameterSpec.Builder parameterSpec = ParameterSpec
                            .builder(propertyInfo.resolve(build, internalTypes), Names.variableName(propertyInfo.getName()));
                    build.withTypeListeners().onSetterMethodDeclaration(build, setSpec,
                            parameterSpec, (TypeDeclaration) propertyInfo.getType().implementation());
                    setSpec.addParameter(
                            parameterSpec.build());
                    typeSpec.addMethod(setSpec.build());
                }
        }

        into.into(typeSpec);
    }

    @Override
    public TypeName getGeneratedJavaType() {

        return interf;
    }
}
