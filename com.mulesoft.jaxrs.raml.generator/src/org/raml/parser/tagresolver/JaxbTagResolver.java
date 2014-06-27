/*
 * Copyright (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.parser.tagresolver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.NodeHandler;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;


public class JaxbTagResolver implements TagResolver
{

    public static final Tag JAXB_TAG = new Tag("!jaxb");

    @Override
    public boolean handles(Tag tag)
    {
        return JAXB_TAG.equals(tag);
    }

    @Override
    public Node resolve(Node node, ResourceLoader resourceLoader, NodeHandler nodeHandler)
    {
        String className = ((ScalarNode) node).getValue();
        try
        {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            JAXBContext context = JAXBContext.newInstance(clazz);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            context.generateSchema(new SchemaOutputResolver()
            {
                @Override
                public Result createOutput(String namespaceURI, String suggestedFileName) throws IOException
                {
                    StreamResult result = new StreamResult(baos);
                    result.setSystemId("001");
                    return result;
                }
            });
            String schema = baos.toString();
            return new ScalarNode(Tag.STR, schema, node.getStartMark(), node.getEndMark(), ((ScalarNode) node).getStyle());
        }
        catch (Exception e)
        {
            throw new YAMLException(e);
        }
    }
}
