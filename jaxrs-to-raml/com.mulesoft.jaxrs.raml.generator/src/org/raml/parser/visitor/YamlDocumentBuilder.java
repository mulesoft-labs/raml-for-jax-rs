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
package org.raml.parser.visitor;

import static org.raml.parser.rule.ValidationMessage.NON_SCALAR_KEY_MESSAGE;
import static org.raml.parser.visitor.TupleType.KEY;
import static org.raml.parser.visitor.TupleType.VALUE;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Stack;

import org.raml.model.Resource;
import org.raml.parser.builder.DefaultTupleBuilder;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.builder.ScalarTupleBuilder;
import org.raml.parser.builder.SequenceBuilder;
import org.raml.parser.builder.SequenceTupleBuilder;
import org.raml.parser.builder.TupleBuilder;
import org.raml.parser.builder.TypeExtraHandler;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

/**
 * <p>YamlDocumentBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public class YamlDocumentBuilder<T> implements NodeHandler
{

    private Class<T> documentClass;
    private T documentObject;
    private Stack<NodeBuilder<?>> builderContext = new Stack<NodeBuilder<?>>();
    private Stack<Object> documentContext = new Stack<Object>();
    private MappingNode rootNode;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;

    /**
     * <p>Constructor for YamlDocumentBuilder.</p>
     *
     * @param documentClass a {@link java.lang.Class} object.
     * @param resourceLoader a {@link org.raml.parser.loader.ResourceLoader} object.
     * @param tagResolvers a {@link org.raml.parser.tagresolver.TagResolver} object.
     */
    public YamlDocumentBuilder(Class<T> documentClass, ResourceLoader resourceLoader, TagResolver... tagResolvers)
    {
        this.documentClass = documentClass;
        this.resourceLoader = resourceLoader;
        this.tagResolvers = tagResolvers;
    }

    /**
     * <p>build.</p>
     *
     * @param content a {@link java.io.Reader} object.
     * @return a T object.
     */
    public T build(Reader content)
    {
        Yaml yamlParser = new Yaml();
        NodeVisitor nodeVisitor = new NodeVisitor(this, resourceLoader, tagResolvers);
        rootNode = (MappingNode) yamlParser.compose(content);
        preBuildProcess();
        nodeVisitor.visitDocument(rootNode);
        postBuildProcess();
        return documentObject;
    }
    
    /**
     * <p>build.</p>
     *
     * @param content a {@link org.yaml.snakeyaml.nodes.MappingNode} object.
     * @return a T object.
     */
    public T build(MappingNode content)
    {
        NodeVisitor nodeVisitor = new NodeVisitor(this, resourceLoader, tagResolvers);
        rootNode =content;
        preBuildProcess();
        nodeVisitor.visitDocument(rootNode);
        postBuildProcess();
        return documentObject;
    }

    /**
     * <p>Getter for the field <code>documentObject</code>.</p>
     *
     * @return a T object.
     */
    protected T getDocumentObject()
    {
        return documentObject;
    }

    /**
     * <p>Getter for the field <code>builderContext</code>.</p>
     *
     * @return a {@link java.util.Stack} object.
     */
    protected Stack<NodeBuilder<?>> getBuilderContext()
    {
        return builderContext;
    }

    /**
     * <p>Getter for the field <code>documentContext</code>.</p>
     *
     * @return a {@link java.util.Stack} object.
     */
    protected Stack<Object> getDocumentContext()
    {
        return documentContext;
    }

    /**
     * <p>Getter for the field <code>resourceLoader</code>.</p>
     *
     * @return a {@link org.raml.parser.loader.ResourceLoader} object.
     */
    protected ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    /**
     * <p>preBuildProcess.</p>
     */
    protected void preBuildProcess()
    {
    }

    /**
     * <p>postBuildProcess.</p>
     */
    protected void postBuildProcess()
    {
    }

    /**
     * <p>build.</p>
     *
     * @param content a {@link java.io.InputStream} object.
     * @return a T object.
     */
    public T build(InputStream content)
    {
        return build(new InputStreamReader(content));
    }

    /**
     * <p>build.</p>
     *
     * @param content a {@link java.lang.String} object.
     * @return a T object.
     */
    public T build(String content)
    {
        return build(new StringReader(content));
    }

    /**
     * <p>Getter for the field <code>rootNode</code>.</p>
     *
     * @return a {@link org.yaml.snakeyaml.nodes.MappingNode} object.
     */
    public MappingNode getRootNode()
    {
        return rootNode;
    }

    
    /** {@inheritDoc} */
    public void onMappingNodeStart(MappingNode mappingNode, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            throw new YAMLException(NON_SCALAR_KEY_MESSAGE + ": " + mappingNode.getStartMark());
        }
        NodeBuilder<?> currentBuilder = builderContext.peek();
        Object parentObject = documentContext.peek();
        if (!(currentBuilder instanceof ScalarTupleBuilder)){
        	Object object = ((TupleBuilder<?, MappingNode>) currentBuilder).buildValue(parentObject, mappingNode);
        	documentContext.push(object);
        }
        else{ 
        	documentContext.push(null);
        }
        
        
        
    }

    
    /** {@inheritDoc} */
    public void onMappingNodeEnd(MappingNode mappingNode, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            throw new YAMLException(NON_SCALAR_KEY_MESSAGE + ": " + mappingNode.getStartMark());
        }
        documentContext.pop();
    }

    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            throw new YAMLException(NON_SCALAR_KEY_MESSAGE + ": " + node.getStartMark());
        }
        SequenceBuilder currentBuilder = (SequenceBuilder) builderContext.peek();
        Object parentObject = documentContext.peek();
        Object object = ((NodeBuilder) currentBuilder).buildValue(parentObject, node);
        builderContext.push(currentBuilder.getItemBuilder());
        documentContext.push(object);
    }

    
    /** {@inheritDoc} */
    public void onSequenceEnd(SequenceNode node, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            throw new YAMLException(NON_SCALAR_KEY_MESSAGE + ": " + node.getStartMark());
        }
        documentContext.pop();
        builderContext.pop();
    }

    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void onScalar(ScalarNode node, TupleType tupleType)
    {

        NodeBuilder<?> currentBuilder = builderContext.peek();
        Object parentObject = documentContext.peek();

        if (tupleType == VALUE)
        {
        	if (currentBuilder instanceof SequenceTupleBuilder){
        		if (node.getValue().length()==0){
        			return;
        		}
        	}
            ((NodeBuilder<ScalarNode>) currentBuilder).buildValue(parentObject, node);
        }
        else
        {
            ((TupleBuilder<ScalarNode, ?>) currentBuilder).buildKey(parentObject, node);
        }

    }


    
    /** {@inheritDoc} */
    public void onDocumentStart(MappingNode node)
    {
        try
        {
            documentObject = documentClass.newInstance();
            documentContext.push(documentObject);
            builderContext.push(buildDocumentBuilder());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private TupleBuilder<?, ?> buildDocumentBuilder()
    {
        DefaultTupleBuilder<Node, MappingNode> documentBuilder = new DefaultTupleBuilder<Node, MappingNode>(new DefaultTupleHandler());
        documentBuilder.addBuildersFor(documentClass);
        return documentBuilder;
    }


    
    /** {@inheritDoc} */
    public void onDocumentEnd(MappingNode node)
    {
        if (documentObject != documentContext.pop())
        {
            throw new IllegalStateException("more zombies?!");
        }
    }

    
    /** {@inheritDoc} */
    public void onTupleEnd(NodeTuple nodeTuple)
    {
        builderContext.pop();

    }

    
    /** {@inheritDoc} */
    public void onTupleStart(NodeTuple nodeTuple)
    {
    	if (nodeTuple.getKeyNode() instanceof ScalarNode){
    		if (documentContext.peek() instanceof Resource){
    			String c=((ScalarNode) nodeTuple.getKeyNode()).getValue();
    			if (c.equals("type")){
    				new TypeExtraHandler().handle(documentContext.peek(),new SequenceNode(Tag.BOOL, Collections.singletonList(nodeTuple.getValueNode()), true));
    			}
    		}
    	}
        TupleBuilder<?, ?> currentBuilder = (TupleBuilder<?, ?>) builderContext.peek();
        if (currentBuilder != null)
        {
            NodeBuilder<?> builder = currentBuilder.getBuilderForTuple(nodeTuple);
            builderContext.push(builder);
        }
        else
        {
            throw new IllegalStateException("Unexpected builderContext state");
        }

    }

    
    /** {@inheritDoc} */
    public void onSequenceElementStart(Node sequenceNode)
    {
    }

    
    /** {@inheritDoc} */
    public void onSequenceElementEnd(Node sequenceNode)
    {
    }

    
    /** {@inheritDoc} */
    public void onCustomTagStart(Tag tag, Node originalValueNode, Node node)
    {
    }

    
    /** {@inheritDoc} */
    public void onCustomTagEnd(Tag tag, Node originalValueNode, Node node)
    {
    }

    
    /** {@inheritDoc} */
    public void onCustomTagError(Tag tag, Node node, String message)
    {
        if (IncludeResolver.INCLUDE_TAG.equals(tag))
        {
            throw new RuntimeException("resource not found: " + ((ScalarNode) node).getValue());
        }
    }

    /**
     * <p>dumpFromAst.</p>
     *
     * @param rootNode a {@link org.yaml.snakeyaml.nodes.Node} object.
     * @return a {@link java.lang.String} object.
     */
    public static String dumpFromAst(Node rootNode)
    {
        Writer writer = new StringWriter();
        dumpFromAst(rootNode, writer);
        return writer.toString();
    }

    /**
     * <p>dumpFromAst.</p>
     *
     * @param rootNode a {@link org.yaml.snakeyaml.nodes.Node} object.
     * @param output a {@link java.io.Writer} object.
     */
    public static void dumpFromAst(Node rootNode, Writer output)
    {
        if (rootNode == null)
        {
            throw new IllegalArgumentException("rootNode is null");
        }
        DumperOptions dumperOptions = new DumperOptions();
        Tag rootTag = dumperOptions.getExplicitRoot();
        Serializer serializer = new Serializer(new Emitter(output, dumperOptions), new Resolver(),
                                               dumperOptions, rootTag);
        try
        {
            serializer.open();
            serializer.serialize(rootNode);
            serializer.close();
        }
        catch (IOException e)
        {
            throw new YAMLException(e);
        }
    }

}
