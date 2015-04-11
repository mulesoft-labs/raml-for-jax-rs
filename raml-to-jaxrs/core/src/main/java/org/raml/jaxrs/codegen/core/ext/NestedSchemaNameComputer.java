package org.raml.jaxrs.codegen.core.ext;

import org.raml.model.MimeType;

/**
 *
 * Created by Pavel Petrochenko on 12/04/15.
 */
public interface NestedSchemaNameComputer extends GeneratorExtension{

    /**
     *
     * @param mime mime type
     * @return null if nested schema name can not be computed by this extension, or null
     */
    String computeNestedSchemaName(MimeType mime);
}
