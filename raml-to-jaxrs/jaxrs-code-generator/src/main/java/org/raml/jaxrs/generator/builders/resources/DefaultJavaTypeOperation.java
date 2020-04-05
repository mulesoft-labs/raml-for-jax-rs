package org.raml.jaxrs.generator.builders.resources;

import amf.client.model.domain.*;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import org.raml.jaxrs.generator.CurrentBuild;
import org.raml.jaxrs.generator.TypeBasedOperation;

import java.util.Optional;

/**
 * Created. There, you have it.
 */
public class DefaultJavaTypeOperation extends TypeBasedOperation.Default<Optional<TypeName>> {

    private final CurrentBuild build;
    private final String packageName;

    private DefaultJavaTypeOperation(CurrentBuild build, String packageName) {
        super((x) -> Optional.ofNullable(build.fetchTypeName(x)));
        this.build = build;
        this.packageName = packageName;
    }

    public static DefaultJavaTypeOperation defaultJavaType(CurrentBuild build, String packageName) {
        return new DefaultJavaTypeOperation(build, packageName);
    }

    @Override
    public Optional<TypeName> on(SchemaShape schemaShape) {
        if (false) {
            return Optional.of(ClassName.get("", "SchemaShape"));
        } else {
            return Optional.of(ClassName.get(packageName, "SchemaShape"));
        }
    }
}
