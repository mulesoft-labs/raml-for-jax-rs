package org.raml.jaxrs.generator.v10;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.internal.impl.commons.model.type.ObjectTypeDeclaration;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.snakeyaml.SYArrayNode;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jean-Philippe Belanger on 11/14/16.
 * Just potential zeroes and ones
 */
public class ModelFixer {

    public static List<TypeDeclaration> parentTypes(Collection<TypeDeclaration> allTypes, TypeDeclaration type) {

        List<String> typeNames = pullOutNames(type);
        List<TypeDeclaration> result = new ArrayList<>();

        for (String typeName : typeNames) {

            for (TypeDeclaration oneType : allTypes) {
                if (typeName.equals(oneType.name())) {

                    result.add(oneType);
                    result.addAll(parentTypes(allTypes, oneType));
                }
            }
        }

        return result;
    }

    // This is seriously a hack.
    private static List<String> pullOutNames(TypeDeclaration decl) {

        InvocationHandler handler = Proxy.getInvocationHandler(decl);
        try {
            Field delegate = handler.getClass().getDeclaredField("delegate");
            delegate.setAccessible(true);
            ObjectTypeDeclaration otd = (org.raml.v2.internal.impl.commons.model.type.ObjectTypeDeclaration) delegate.get(handler);
            final Node typeNode = otd.getNode().get("type");

            if ( typeNode instanceof SYArrayNode) {
                List<String> types = Lists.transform(typeNode.getChildren(), new Function<Node, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Node input) {
                        return input.toString();
                    }
                });

                return types;
            } else {

                return Arrays.asList(typeNode.toString());
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return Collections.emptyList();
        }

    }
}
