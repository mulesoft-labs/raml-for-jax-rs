package org.raml.jaxrs.generator;

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
    /** Constant <code>GENERIC_PAYLOAD_ARGUMENT_NAME="entity"</code> */
    public static final String GENERIC_PAYLOAD_ARGUMENT_NAME = "entity";
    /** Constant <code>MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME="headers"</code> */
    public static final String MULTIPLE_RESPONSE_HEADERS_ARGUMENT_NAME = "headers";
    /** Constant <code>EXAMPLE_PREFIX=" e.g. "</code> */
    public static final String EXAMPLE_PREFIX = " e.g. ";

    public static String buildResourceInterfaceName(String resourceInterfaceName)
    {
        return isBlank(resourceInterfaceName) ? "Root" : buildJavaFriendlyName(resourceInterfaceName);
    }

/*
    */
/**
     * <p>buildVariableName.</p>
     *
     * @param source a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     *//*

    public static String buildVariableName(final String source)
    {
        final String name = uncapitalize(buildJavaFriendlyName(source));

        return Constants.JAVA_KEYWORDS.contains(name) ? "$" + name : name;
    }

    */
/**
     * <p>buildJavaFriendlyName.</p>
     *
     * @param source a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */

    public static String buildJavaFriendlyName(final String source)
    {
        final String baseName = source.replaceAll("[\\W_]", " ");

        String friendlyName = capitalize(baseName).replaceAll("[\\W_]", "");

        if (isDigits(left(friendlyName, 1)))
        {
            friendlyName = "_" + friendlyName;
        }

        return friendlyName;
    }

/**
     * <p>buildResourceMethodName.</p>
     *
     * @param action a {@link org.aml.apimodel.Action} object.
     * @param bodyMimeType a {@link org.aml.apimodel.MimeType} object.
     * @return a {@link java.lang.String} object.
     *//*

    public static String buildResourceMethodName(final Action action, final MimeType bodyMimeType)
    {
        final String methodBaseName = buildJavaFriendlyName(action.resource()
                .getUri()
                .replace("{", " By "));

        return action.method().toString().toLowerCase() + buildMimeTypeInfix(bodyMimeType) + methodBaseName;
    }

    */
/**
     * <p>buildResponseMethodName.</p>
     *
     * @param statusCode a int.
     * @param mimeType a {@link org.aml.apimodel.MimeType} object.
     * @return a {@link java.lang.String} object.
     *//*

    public static String buildResponseMethodName(final int statusCode, final MimeType mimeType)
    {
        final String status = EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, DEFAULT_LOCALE);

        String string = getShortMimeType(mimeType)
                + buildJavaFriendlyName(defaultIfBlank(status, "_" + statusCode));
        return "with" + Character.toUpperCase(string.charAt(0))+string.substring(1);
    }


    */
/**
     * <p>buildMimeTypeInfix.</p>
     *
     * @param bodyMimeType a {@link org.aml.apimodel.MimeType} object.
     * @return a {@link java.lang.String} object.
     *//*

    public static String buildMimeTypeInfix(final MimeType bodyMimeType)
    {
        return bodyMimeType != null ? buildJavaFriendlyName(getShortMimeType(bodyMimeType)) : "";
    }

    */
/**
     * <p>getShortMimeType.</p>
     *
     * @param mimeType a {@link org.aml.apimodel.MimeType} object.
     * @return a {@link java.lang.String} object.
     *//*

    public static String getShortMimeType(final MimeType mimeType)
    {
        if (mimeType == null) {
            return "";
        }
        String subType = StringUtils.substringAfter(mimeType.getType()
                .toLowerCase(DEFAULT_LOCALE), "/");

        if (subType.contains(".")) {
            // handle types like application/vnd.example.v1+json
            StringBuilder sb = new StringBuilder();
            for (String s : subType.split("\\W+")) {
                sb.append(sb.length() == 0 ? s : StringUtils.capitalize(s));
            }
            return sb.toString();
        } else {
            // handle any other types
            return remove(remove(remove(subType, "x-www-"), "+"), "-");
        }

    }

    */
/**
     * get enum field name from value
     *
     * @param value a {@link java.lang.String} object.
     * @return  a {@link java.lang.String} object.
     *//*

    public static boolean canBenumConstantName(final String value){
        boolean res=value.length()>0;
        for (int i=0;i<value.length();i++){
            char c=value.charAt(i);
            if (i==0){
                res&=Character.isJavaIdentifierStart(c);
            }
            else {
                res&=Character.isJavaIdentifierPart(c);
            }
            if (!res){
                break;
            }
        }
        return res;
    }

    */
/**
     * <p>isValidEnumValues.</p>
     *
     * @param values a {@link java.util.List} object.
     * @return true if this list of strings can be used as names for enum
     *//*

    public static boolean isValidEnumValues(java.util.List<String>values){
        boolean res=values.size()>0;
        for (String v:values){
            res&=canBenumConstantName(v);
            if (!res){
                break;
            }
        }
        return res;
    }


    private Names()
    {
        throw new UnsupportedOperationException();
    }
*/
}
