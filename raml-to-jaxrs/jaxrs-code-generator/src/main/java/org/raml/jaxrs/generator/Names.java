package org.raml.jaxrs.generator;

import com.google.common.base.Ascii;
import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.math.NumberUtils.compare;
import static org.apache.commons.lang.math.NumberUtils.isDigits;

/**
 * <p>Names class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class Names
{

    public static String typeName(String... name)
    {
        if ( name.length == 1 && isBlank(name[0])) {

            return "Root";
        }

        List<String> values = new ArrayList<>();
        for (String s : name) {
            if ( s.matches(".*[^a-zA-Z0-9].*")) {

                values.add(buildJavaFriendlyName(s, CaseFormat.UPPER_CAMEL));
            } else {
                values.add(firstCharToUpper(s));
            }
        }
        return Strings.join(values, "");
    }

    public static String methodName(String... name) {

        return variableName(name);
    }

    public static String variableName(String... name)
    {
        if ( name.length == 1 && isBlank(name[0])) {

            return "root";
        }

        List<String> values = new ArrayList<>();
        for (int i = 0; i < name.length; i ++) {
            String s = name[i];
            if ( s.matches(".*[^a-zA-Z0-9].*")) {

                if ( i == 0 ) {
                    values.add(buildJavaFriendlyName(s, CaseFormat.LOWER_CAMEL));
                } else {
                    values.add(buildJavaFriendlyName(s, CaseFormat.UPPER_CAMEL));
                }
            } else {
                if ( i == 0) {
                    values.add(firstCharToLower(s));
                } else {
                    values.add(firstCharToUpper(s));
                }
            }
        }
        return Strings.join(values, "");
    }

    public static String constantName(String value) {

        return buildJavaFriendlyName(value, CaseFormat.UPPER_UNDERSCORE);
    }

    public static String resourceMethodName(GResource resource, GMethod method) {

        if ( resource.uriParameters().size() == 0) {

            return Names.methodName(method.method(), resource.resourcePath().replaceAll("\\{[^}]+}", ""));
        } else {

            List<String> elements = new ArrayList<>();
            elements.add(method.method());
            elements.add(resource.resourcePath().replaceAll("\\{[^}]+\\}", ""));
            elements.add("By");
            List<String> uriparam = Lists.transform(resource.uriParameters(), new Function<GParameter, String>() {
                @Nullable
                @Override
                public String apply(@Nullable GParameter input) {
                    return input.name();
                }
            });

            for(int i = 0; i < uriparam.size(); i ++ ) {
                elements.add(uriparam.get(i));
                if ( i < uriparam.size() - 1 ) {

                    elements.add("and");
                }
            }

            return Names.methodName(elements.toArray(new String[elements.size()]));
        }
    }

    public static String responseClassName(GResource resource, GMethod method) {

        if ( resource.uriParameters().size() == 0) {

            return Names.typeName(method.method(), resource.resourcePath().replaceAll("\\{[^}]+}", ""), "Response");
        } else {

            List<String> elements = new ArrayList<>();
            elements.add(method.method());
            elements.add(resource.resourcePath().replaceAll("\\{[^}]+\\}", ""));
            elements.add("By");
            List<String> uriparam = Lists.transform(resource.uriParameters(), new Function<GParameter, String>() {
                @Nullable
                @Override
                public String apply(@Nullable GParameter input) {
                    return input.name();
                }
            });

            for(int i = 0; i < uriparam.size(); i ++ ) {
                elements.add(uriparam.get(i));
                if ( i < uriparam.size() - 1 ) {

                    elements.add("and");
                }
            }
            elements.add("Response");

            return Names.typeName(elements.toArray(new String[elements.size()]));
        }
    }


    public static String javaTypeName(Resource resource, Method method, TypeDeclaration declaration) {
        return typeName(resource.resourcePath(), method.method(), declaration.name());
    }

    public static String ramlTypeName(Resource resource, Method method, TypeDeclaration declaration) {
        return resource.resourcePath() + method.method() + declaration.name();
    }

    public static String javaTypeName(Resource resource, Method method, Response response, TypeDeclaration declaration) {
        return typeName(resource.resourcePath(), method.method(), response.code().value(), declaration.name());
    }

    public static String ramlTypeName(Resource resource, Method method, Response response, TypeDeclaration declaration) {
        return resource.resourcePath() + method.method() + response.code().value() + declaration.name();
    }


    private Names()
    {
        throw new UnsupportedOperationException();
    }


    private static String firstCharToUpper(String word) {
        return (word.isEmpty())
                ? word
                : new StringBuilder(word.length())
                .append(Ascii.toUpperCase(word.charAt(0)))
                .append(word.substring(1))
                .toString();
    }

    private static String firstCharToLower(String word) {
        return (word.isEmpty())
                ? word
                : new StringBuilder(word.length())
                .append(Ascii.toLowerCase(word.charAt(0)))
                .append(word.substring(1))
                .toString();
    }

    /**
     * <p>buildJavaFriendlyName.</p>
     *
     * @param source a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    private static String buildJavaFriendlyName(final String source, CaseFormat format)
    {
        final String baseName = source.replaceAll("\\W+", "_").replaceAll("^_+", "").replaceAll("[^\\w_]", "");

        String friendlyName = CaseFormat.LOWER_UNDERSCORE.to(format, baseName);

        if (isDigits(left(friendlyName, 1)))
        {
            friendlyName = "_" + friendlyName;
        }

        return friendlyName;
    }


    public static String ramlTypeName(org.raml.v2.api.model.v08.resources.Resource resource,
            org.raml.v2.api.model.v08.methods.Method method, BodyLike typeDeclaration) {

        return resource.resourcePath() + method.method() + typeDeclaration.name();
    }

    public static String ramlTypeName(org.raml.v2.api.model.v08.resources.Resource resource,
            org.raml.v2.api.model.v08.methods.Method method, org.raml.v2.api.model.v08.bodies.Response response,
            BodyLike typeDeclaration) {

        return resource.resourcePath() + method.method() + response.code().value() + typeDeclaration.name();
    }

    public static String javaTypeName(org.raml.v2.api.model.v08.resources.Resource resource,
            org.raml.v2.api.model.v08.methods.Method method, BodyLike typeDeclaration) {
        return typeName(resource.resourcePath(), method.method(), typeDeclaration.name());
    }

    public static String javaTypeName(org.raml.v2.api.model.v08.resources.Resource resource,
            org.raml.v2.api.model.v08.methods.Method method, org.raml.v2.api.model.v08.bodies.Response response,
            BodyLike typeDeclaration) {
        return typeName(resource.resourcePath(), method.method(), response.code().value(), typeDeclaration.name());
    }

}
