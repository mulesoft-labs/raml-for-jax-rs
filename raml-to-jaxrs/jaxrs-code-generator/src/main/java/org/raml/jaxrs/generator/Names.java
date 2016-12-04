package org.raml.jaxrs.generator;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.resources.Resource;

import javax.annotation.Nullable;
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

    public static String buildTypeName(String resourceInterfaceName)
    {
        return isBlank(resourceInterfaceName) ? "Root" : buildJavaFriendlyName(resourceInterfaceName, CaseFormat.UPPER_CAMEL);
    }

    public static String buildVariableName(String resourceInterfaceName)
    {
        return isBlank(resourceInterfaceName) ? "Root" : buildJavaFriendlyName(resourceInterfaceName, CaseFormat.LOWER_CAMEL);
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

    public static String parameterNameMethodSuffix(List<String> names) {

        if ( names.size() == 0 ) {
            return "";
        }

        String s = Strings.join(names, "_and_");
        String suffix = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s);

        return "By_" + suffix;
    }


    public static String methodName(Resource resource, Method method) {

        if ( resource.uriParameters().size() == 0) {

            return Names.buildVariableName(method.method() + "_" +
                    resource.relativeUri().value().replaceAll("\\{[^}]+}", ""));
        } else {

            return Names.buildVariableName(method.method() + "_"
                    + resource.relativeUri().value().replaceAll("\\{[^}]+}", "")
                    + parameterNameMethodSuffix(Lists.transform(
                    resource.uriParameters(), new Function<TypeDeclaration, String>() {
                        @Nullable
                        @Override
                        public String apply(@Nullable TypeDeclaration input) {
                            return input.name();
                        }
                    })));
        }
    }

    public static String responseClassName(Resource resource, Method method) {

        if ( resource.uriParameters().size() == 0) {

            return Names.buildTypeName(method.method() + "_" + resource.relativeUri().value().replaceAll("\\{[^}]+}", "") + "_Response");
        } else {

            return Names.buildTypeName(
                    method.method() + "_" + resource.relativeUri().value().replaceAll("\\{[^}]+\\}", "")
                    + parameterNameMethodSuffix(Lists.transform(resource.uriParameters(), new Function<TypeDeclaration, String>() {
                        @Nullable
                        @Override
                        public String apply(@Nullable TypeDeclaration input) {
                            return input.name();
                        }
                    })) + "_Response");
        }
    }

    public static String methodNameSuffix(String resourcePath, List<String> queryParameters) {

        String methodNamePathPart = "".equals(resourcePath) ? "": Names.buildTypeName(resourcePath);
        String queryParameterSuffix = Names.parameterNameMethodSuffix(queryParameters);

        return methodNamePathPart + queryParameterSuffix;
    }

    private Names()
    {
        throw new UnsupportedOperationException();
    }


    public static String javaTypeName(Resource resource, Method method, TypeDeclaration declaration) {
        return buildTypeName(resource.resourcePath() + "_" +  method.method() + "_" + declaration.name());
    }

    public static String ramlTypeName(Resource resource, Method method, TypeDeclaration declaration) {
        return resource.resourcePath() + method.method() + declaration.name();
    }

    public static String javaTypeName(Resource resource, Method method, Response response, TypeDeclaration declaration) {
        return buildTypeName(resource.resourcePath() + "_" +  method.method() + "_" + response.code().value() + "_" + declaration.name());
    }

    public static String ramlTypeName(Resource resource, Method method, Response response, TypeDeclaration declaration) {
        return resource.resourcePath() + method.method() + response.code().value() + declaration.name();
    }
}
