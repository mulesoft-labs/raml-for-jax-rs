package org.raml.jaxrs.generator;

import com.google.common.base.CaseFormat;

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
        final String baseName = source.replaceAll("\\s+", "_").replaceAll("[^\\w_]", "");

        String friendlyName = CaseFormat.LOWER_UNDERSCORE.to(format, baseName);

        if (isDigits(left(friendlyName, 1)))
        {
            friendlyName = "_" + friendlyName;
        }

        return friendlyName;
    }

    private Names()
    {
        throw new UnsupportedOperationException();
    }
}
