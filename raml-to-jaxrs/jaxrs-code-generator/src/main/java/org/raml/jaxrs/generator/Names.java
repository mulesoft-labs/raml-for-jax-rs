package org.raml.jaxrs.generator;

import com.google.common.base.CaseFormat;
import joptsimple.internal.Strings;

import java.util.List;

import static org.apache.commons.lang.StringUtils.defaultIfBlank;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.left;
import static org.apache.commons.lang.StringUtils.remove;
import static org.apache.commons.lang.StringUtils.uncapitalize;
import static org.apache.commons.lang.WordUtils.capitalize;
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

        return "By" + suffix;
    }

    private Names()
    {
        throw new UnsupportedOperationException();
    }
}
