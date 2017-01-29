package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.CodeContainer;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.builders.extensions.types.TypeContextImpl;
import org.raml.jaxrs.generator.extension.types.TypeExtension;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;

import java.io.IOException;

/**
 * Created by Jean-Philippe Belanger on 1/29/17.
 * Just potential zeroes and ones
 */
class SimpleTypeGenerator implements JavaPoetTypeGenerator {
    private final V10GType originalType;
    private final V10TypeRegistry registry;
    private final CurrentBuild currentBuild;

    public SimpleTypeGenerator(V10GType originalType, V10TypeRegistry registry, CurrentBuild currentBuild) {
        this.originalType = originalType;
        this.registry = registry;
        this.currentBuild = currentBuild;
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory, BuildPhase buildPhase) throws IOException {

        TypeExtension typeExtension = new SimpleInheritanceExtension(originalType, registry, currentBuild);
        rootDirectory.into(typeExtension.onType(new TypeContextImpl(currentBuild) {
            @Override
            public void addImplementation() {
                currentBuild.newImplementation(SimpleTypeGenerator.this);
            }

            @Override
            public void createInternalClass(JavaPoetTypeGenerator internalGenerator) {
                currentBuild.internalClass(SimpleTypeGenerator.this, internalGenerator);
            }
        }, null, originalType, buildPhase));
    }

    @Override
    public TypeName getGeneratedJavaType() {
        return originalType.defaultJavaTypeName(currentBuild.getModelPackage());
    }

    @Override
    public void output(CodeContainer<TypeSpec.Builder> rootDirectory) throws IOException {

        output(rootDirectory, null);
    }
}
