package org.raml.jaxrs.generator.v10.types;

import com.squareup.javapoet.ClassName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.builders.BuildPhase;
import org.raml.jaxrs.generator.builders.JavaPoetTypeGenerator;
import org.raml.jaxrs.generator.builders.TypeGenerator;
import org.raml.jaxrs.generator.ramltypes.GType;
import org.raml.jaxrs.generator.v10.EnumerationGenerator;
import org.raml.jaxrs.generator.v10.UnionTypeGenerator;
import org.raml.jaxrs.generator.v10.V10GType;
import org.raml.jaxrs.generator.v10.V10TypeRegistry;

/**
 * Created by Jean-Philippe Belanger on 12/30/16.
 * Just potential zeroes and ones
 */
class V10TypeFactory {

    static TypeGenerator createObjectType(final V10TypeRegistry registry, final CurrentBuild currentBuild,
            final V10GType originalType, boolean publicType) {

        TypeGenerator generator = new SimpleTypeGenerator(originalType, registry, currentBuild);

        if (publicType) {
            currentBuild.newGenerator(originalType.name(), generator);
        }
        return generator;
    }

    static TypeGenerator createEnumerationType(CurrentBuild currentBuild, GType type) {
        JavaPoetTypeGenerator generator =  new EnumerationGenerator(
                currentBuild,
                ((V10GType)type).implementation(),
                (ClassName) type.defaultJavaTypeName(currentBuild.getModelPackage()),
                type.enumValues());

        currentBuild.newGenerator(type.name(), generator);
        return generator;
    }


    public static void createUnion(CurrentBuild currentBuild, V10TypeRegistry v10TypeRegistry, V10GType v10GType) {

        ClassName unionJavaName = (ClassName) v10GType.defaultJavaTypeName(currentBuild.getModelPackage());
        currentBuild.newGenerator(v10GType.name(), new UnionTypeGenerator(v10TypeRegistry, v10GType, unionJavaName, currentBuild));
    }

}
