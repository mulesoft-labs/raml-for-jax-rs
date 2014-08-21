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
import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.raml.parser.tagresolver.IncludeResolver.INCLUDE_TAG;
import static org.yaml.snakeyaml.nodes.NodeId.mapping;
import static org.yaml.snakeyaml.nodes.NodeId.scalar;
import static org.yaml.snakeyaml.nodes.NodeId.sequence;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Resource;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.utils.Inflector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class TemplateResolver
{

    public static final String OPTIONAL_MODIFIER = "?";
    public static final String ALL_ACTIONS = "*";
    public static final String TRAIT_USE_KEY = "is";
    public static final String RESOURCE_TYPE_USE_KEY = "type";
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private IncludeResolver includeResolver = new IncludeResolver();
    private Map<String, MappingNode> resourceTypesMap = new HashMap<String, MappingNode>();
    private Map<String, MappingNode> traitsMap = new HashMap<String, MappingNode>();
    private ResourceLoader resourceLoader;
    private NodeHandler nodeNandler;
    private Set<MappingNode> resolvedNodes = new HashSet<MappingNode>();
	private boolean doResolve;

    private enum TemplateType
    {
        RESOURCE_TYPE, TRAIT
    }

    public TemplateResolver(ResourceLoader resourceLoader, NodeHandler nodeNandler,boolean doResolve)
    {
        this.resourceLoader = resourceLoader;
        this.nodeNandler = nodeNandler;
        this.doResolve=doResolve;
    }

    public Map<String, MappingNode> getResourceTypesMap()
    {
        return resourceTypesMap;
    }

    public Map<String, MappingNode> getTraitsMap()
    {
        return traitsMap;
    }

    public List<ValidationResult> init(MappingNode rootNode)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

        if (rootNode == null)
        {
            validationResults.add(createErrorResult("Invalid Root Node"));
            return validationResults;
        }

        for (int i = 0; i < rootNode.getValue().size(); i++)
        {
            NodeTuple rootTuple = rootNode.getValue().get(i);

            Node keyNode = rootTuple.getKeyNode();
            if (keyNode.getNodeId() != scalar)
            {
                continue; //invalid key
            }
            String key = ((ScalarNode) keyNode).getValue();
            if (key.equals("resourceTypes") || key.equals("traits"))
            {
                Node templateSequence = rootTuple.getValueNode();
                if (templateSequence.getNodeId() == scalar)
                {
                    if (!templateSequence.getTag().equals(INCLUDE_TAG))
                    {
                        validationResults.add(createErrorResult("Sequence or !include expected", templateSequence));
                        break;
                    }
                    templateSequence = includeResolver.resolve(templateSequence, resourceLoader, nodeNandler);
                    rootNode.getValue().remove(i);
                    rootNode.getValue().add(i, new NodeTuple(keyNode, templateSequence));
                }
                if (templateSequence.getNodeId() != sequence)
                {
                    validationResults.add(createErrorResult("Sequence expected", templateSequence));
                    rootNode.getValue().remove(i);
                    rootNode.getValue().add(i, new NodeTuple(keyNode, new SequenceNode(Tag.SEQ, new ArrayList<Node>(), false)));
                    break;
                }
                loopTemplateSequence((SequenceNode) templateSequence, key, validationResults);
            }
        }
        return validationResults;
    }

    private void loopTemplateSequence(SequenceNode templateSequence, String templateType, List<ValidationResult> validationResults)
    {
        List<Node> prunedTemplates = new ArrayList<Node>();
        for (int j = 0; j < templateSequence.getValue().size(); j++)
        {
            Node template = templateSequence.getValue().get(j);
            if (template.getNodeId() == scalar)
            {
                if (!template.getTag().equals(INCLUDE_TAG))
                {
                    validationResults.add(createErrorResult("Mapping or !include expected", templateSequence.getStartMark(), templateSequence.getEndMark()));
                }
                template = includeResolver.resolve(template, resourceLoader, nodeNandler);
            }
            if (template.getNodeId() == mapping)
            {
            	MappingNode map=(MappingNode) template;
            	List<NodeTuple> value = map.getValue();
            	ArrayList<NodeTuple>tn=new ArrayList<NodeTuple>();
            	for (NodeTuple t:new ArrayList<NodeTuple>(value)){
            		if( t.getValueNode() instanceof ScalarNode){
            			ScalarNode vn = (ScalarNode) t.getValueNode();
            			 if (!vn.getTag().equals(INCLUDE_TAG))
                         {
                             validationResults.add(createErrorResult("Mapping or !include expected", templateSequence.getStartMark(), templateSequence.getEndMark()));
                         }
                        Node resolvedNode = includeResolver.resolve(vn, resourceLoader, nodeNandler);
                        NodeTuple e = new NodeTuple(t.getKeyNode(), resolvedNode);
				        int indexOf = value.indexOf(t);
                        value.set(indexOf, e);
            		}
            	}	
            }
            for (NodeTuple tuple : ((MappingNode) template).getValue())
            {
                if (tuple.getKeyNode().getNodeId() != scalar)
                {
                    validationResults.add(createErrorResult(NON_SCALAR_KEY_MESSAGE, tuple.getKeyNode()));
                    continue;
                }
                String templateKey = ((ScalarNode) tuple.getKeyNode()).getValue();
                Node templateValue = tuple.getValueNode();
                if (templateValue.getNodeId()==scalar){
                	ScalarNode ns=(ScalarNode) templateValue;
                	if (ns.getValue().equals("invalid")){
                		templateValue=new MappingNode(Tag.MAP, new ArrayList(), null);
                	}
                }
                if (templateValue.getNodeId() != mapping)
                {
                	
                    validationResults.add(createErrorResult("Mapping expected", templateValue.getStartMark(), templateValue.getEndMark()));
                    
                    continue;
                }
                if (templateType.equals("resourceTypes"))
                {
                    resourceTypesMap.put(templateKey, (MappingNode) templateValue);
                }
                if (templateType.equals("traits"))
                {
                    traitsMap.put(templateKey, (MappingNode) templateValue);
                }
                prunedTemplates.add(getFakeTemplateNode(tuple.getKeyNode()));
            }
        }
        templateSequence.getValue().clear();
        templateSequence.getValue().addAll(prunedTemplates);
    }

    private Node getFakeTemplateNode(Node keyNode)
    {
        List<NodeTuple> innerTuples = new ArrayList<NodeTuple>();
        innerTuples.add(new NodeTuple(new ScalarNode(Tag.STR, "displayName", null, null, null), keyNode));
        MappingNode innerNode = new MappingNode(Tag.MAP, innerTuples, false);
        List<NodeTuple> outerTuples = new ArrayList<NodeTuple>();
        outerTuples.add(new NodeTuple(keyNode, innerNode));
        return new MappingNode(Tag.MAP, outerTuples, false);
    }

    public List<ValidationResult> resolve(MappingNode resourceNode, String relativeUri, String fullUri)
    {
        List<ValidationResult> templateValidations = new ArrayList<ValidationResult>();

        //avoid processing resources already processed (yaml references)
        if (resolvedNodes.contains(resourceNode))
        {
            return templateValidations;
        }
        resolvedNodes.add(resourceNode);

        return new ResourceTemplateMerger(templateValidations, resourceNode, relativeUri, fullUri).merge();
    }

    private class ResourceTemplateMerger
    {

        private List<ValidationResult> templateValidations;
        private MappingNode resourceNode;
        private String relativeUri;
        private String fullUri;
        private String currentAction;

        public ResourceTemplateMerger(List<ValidationResult> templateValidations, MappingNode resourceNode, String relativeUri, String fullUri)
        {
            this.templateValidations = templateValidations;
            this.resourceNode = resourceNode;
            this.relativeUri = relativeUri;
            this.fullUri = fullUri;
        }

        public List<ValidationResult> merge()
        {
            if (mergeTemplatesIfNeeded(resourceNode, new HashMap<String, Node>()))
            {
                removeOptionalNodes(resourceNode, true);
            }
            return templateValidations;
        }

        private boolean mergeTemplatesIfNeeded(MappingNode resourceNode, Map<String, Node> globalActionNodes)
        {
            Node typeReference = null;
            Map<String, SequenceNode> traitsReference = new HashMap<String, SequenceNode>();
            HashMap<String, Node> actionNodes = new HashMap<String, Node>(globalActionNodes);

            for (int i = 0; i < resourceNode.getValue().size(); i++)
            {
            	if (!doResolve){
            		break;
            	}
                NodeTuple resourceTuple = resourceNode.getValue().get(i);
                if (resourceTuple.getKeyNode().getNodeId() != scalar)
                {
                    break; //invalid key
                }
                String key = ((ScalarNode) resourceTuple.getKeyNode()).getValue();
                if (key.equals(RESOURCE_TYPE_USE_KEY))
                {
                    typeReference = cloneNode(resourceTuple.getValueNode(), new HashMap<String, String>());
                    removeParametersFromTemplateCall(resourceTuple);
                }
                else if (key.equals(TRAIT_USE_KEY))
                {
                    SequenceNode sequence = cloneSequenceNode((SequenceNode) resourceTuple.getValueNode(), new HashMap<String, String>());
                    traitsReference.put(ALL_ACTIONS, sequence);
                    removeParametersFromTraitsCall(resourceTuple);
                }
                else if (isAction(key))
                {
                    Node actionNode = resourceTuple.getValueNode();
                    if (actionNode.getTag().equals(Tag.NULL))
                    {
                        actionNode = setTupleValueToEmptyMappingNode(resourceTuple);
                    }
                    else if (actionNode.getTag().equals(INCLUDE_TAG))
                    {
                        actionNode = includeResolver.resolve(actionNode, resourceLoader, nodeNandler);
                        resourceNode.getValue().remove(i);
                        resourceNode.getValue().add(i, new NodeTuple(resourceTuple.getKeyNode(), actionNode));
                    }
                    if (actionNode.getNodeId() != mapping)
                    {
                        break;
                    }
                    actionNodes.put(normalizeKey(key), actionNode);
                    for (NodeTuple actionTuple : ((MappingNode) actionNode).getValue())
                    {
                        String actionTupleKey = ((ScalarNode) actionTuple.getKeyNode()).getValue();
                        if (actionTupleKey.equals(TRAIT_USE_KEY))
                        {
                            SequenceNode sequence = cloneSequenceNode((SequenceNode) actionTuple.getValueNode(), new HashMap<String, String>());
                            traitsReference.put(normalizeKey(key), sequence);
                            removeParametersFromTraitsCall(actionTuple);
                        }
                    }
                }

            }

            if (!traitsReference.isEmpty())
            {
                //merge action level tratis
                for (Map.Entry<String, SequenceNode> actionEntry : traitsReference.entrySet())
                {
                    String actionName = actionEntry.getKey();
                    if (!actionName.equals(ALL_ACTIONS))
                    {
                        applyTraitsToActions(actionEntry.getValue(), actionNodes, actionName);
                    }
                }

                //merge resource level traits if there is no parent resource type
                if (traitsReference.get(ALL_ACTIONS) != null && typeReference == null)
                {
                    applyTraitsToActions(traitsReference.get(ALL_ACTIONS), actionNodes, null);
                }
            }

            if (typeReference != null)
            {
                MappingNode clone = cloneTemplate(typeReference, TemplateType.RESOURCE_TYPE);
                if (clone == null)
                {
                    //template not found
                    return false;
                }

                //update global action map
                for (Map.Entry<String, Node> entry : actionNodes.entrySet())
                {
                    if (!globalActionNodes.containsKey(entry.getKey()))
                    {
                        globalActionNodes.put(entry.getKey(), entry.getValue());
                    }
                }

                //merge parent type if defined
                mergeTemplatesIfNeeded(clone, actionNodes);

                //merge resource level traits
                if (traitsReference.get(ALL_ACTIONS) != null)
                {
                    applyTraitsToActions(traitsReference.get(ALL_ACTIONS), actionNodes, null);
                }

                //merge type, no traits (action level traits could be merged)
                mergeNodes(resourceNode, clone, Resource.class);
            }

            return !traitsReference.isEmpty() || typeReference != null;
        }

        private void removeParametersFromTraitsCall(NodeTuple traitsNodeTuple)
        {
            if (traitsNodeTuple.getValueNode().getNodeId() == sequence)
            {
                List<Node> traitList = ((SequenceNode) traitsNodeTuple.getValueNode()).getValue();
                for (int i = 0; i < traitList.size(); i++)
                {
                    Node traitNode = traitList.get(i);
                    if (traitNode.getNodeId() == mapping)
                    {
                        Node keyNode = ((MappingNode) traitNode).getValue().get(0).getKeyNode();
                        if (keyNode.getNodeId() == scalar)
                        {
                            traitList.remove(i);
                            traitList.add(i, keyNode);
                        }
                    }
                }
            }
        }

        private void removeParametersFromTemplateCall(NodeTuple typeNodeTuple)
        {
            if (typeNodeTuple.getValueNode().getNodeId() == mapping)
            {
                NodeTuple typeParamTuple = ((MappingNode) typeNodeTuple.getValueNode()).getValue().get(0);
                try
                {
                    Field value = typeNodeTuple.getClass().getDeclaredField("valueNode");
                    value.setAccessible(true);
                    value.set(typeNodeTuple, typeParamTuple.getKeyNode());
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

        private Node setTupleValueToEmptyMappingNode(NodeTuple tuple)
        {
            try
            {
                Field value = tuple.getClass().getDeclaredField("valueNode");
                value.setAccessible(true);
                MappingNode mappingNode = new MappingNode(Tag.MAP, new ArrayList<NodeTuple>(), false);
                value.set(tuple, mappingNode);
                return mappingNode;
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }

        private void applyTraitsToActions(SequenceNode traits, Map<String, Node> actionNodes, String actionName)
        {
            for (Node ref : traits.getValue())
            {
            	if (!doResolve){
            		break;
            	}
                if (actionName == null)
                {
                    for (Map.Entry<String, Node> actionEntry : actionNodes.entrySet())
                    {
                        currentAction = actionEntry.getKey();
                        MappingNode templateNode = cloneTemplate(ref, TemplateType.TRAIT);
                        if (templateNode != null)
                        {
                            mergeNodes(actionEntry.getValue(), templateNode, Action.class);
                        }
                    }
                }
                else
                {
                    currentAction = actionName;
                    MappingNode templateNode = cloneTemplate(ref, TemplateType.TRAIT);
                    if (templateNode != null)
                    {
                        mergeNodes(actionNodes.get(actionName), templateNode, Action.class);
                    }
                }
            }
        }

        private MappingNode cloneTemplate(Node reference, TemplateType type)
        {
            String templateName = getTemplateName(reference);
            Map<String, MappingNode> templateMap;

            Map<String, String> defaultParameters = new HashMap<String, String>();
            defaultParameters.put("resourcePath", relativeUri);
            defaultParameters.put("resourcePathName", getResourcePathName(fullUri));

            String label;
            if (type == TemplateType.RESOURCE_TYPE)
            {
                templateMap = getResourceTypesMap();
                label = "resource type";
            }
            else
            {
                templateMap = getTraitsMap();
                label = "trait";
                defaultParameters.put("methodName", currentAction);
            }

            MappingNode templateNode = templateMap.get(templateName);
            if (templateNode == null)
            {
                addError(label + " not defined: " + templateName, reference);
                return null;
            }
            return cloneMappingNode(templateNode, getTemplateParameters(reference, defaultParameters));
        }

        private String getResourcePathName(String fullUri)
        {
            String[] paths = fullUri.split("/");
            for (int i = paths.length - 1; i >= 0; i--)
            {
                if (!paths[i].contains("{") && paths[i].length() > 0)
                {
                    return paths[i];
                }
            }
            return "";
        }

        private void addError(String message, Node node)
        {
            templateValidations.add(createErrorResult(message, node));
        }

        private Map<String, String> getTemplateParameters(Node node, Map<String, String> parameters)
        {
            if (node.getNodeId() == mapping)
            {
                List<NodeTuple> tuples = ((MappingNode) node).getValue();
                Node params = tuples.get(0).getValueNode();
                if (params.getTag() == Tag.NULL)
                {
                    return parameters;
                }
                if (params.getNodeId() != mapping)
                {
                    addError("Mapping node expected", params);
                    return parameters;
                }
                for (NodeTuple paramTuple : ((MappingNode) params).getValue())
                {
                    String paramKey = ((ScalarNode) paramTuple.getKeyNode()).getValue();
                    String paramValue = ((ScalarNode) paramTuple.getValueNode()).getValue();
                    parameters.put(paramKey, paramValue);
                }
            }
            return parameters;
        }

        private String getTemplateName(Node templateReferenceNode)
        {
            Node templateNameNode = templateReferenceNode;
            if (templateReferenceNode.getNodeId() == mapping)
            {
                templateNameNode = ((MappingNode) templateReferenceNode).getValue().get(0).getKeyNode();
            }
            return ((ScalarNode) templateNameNode).getValue();
        }

        private void removeOptionalNodes(MappingNode node, boolean isResourceNode)
        {
            for (NodeTuple tuple : new ArrayList<NodeTuple>(node.getValue()))
            {
                String keyValue = ((ScalarNode) tuple.getKeyNode()).getValue();
                if (isResourceNode && keyValue.startsWith("/"))
                {
                    continue;
                }
                if (isOptional(keyValue))
                {
                    node.getValue().remove(tuple);
                }
                else
                {
                    if (tuple.getValueNode().getNodeId() == mapping)
                    {
                        removeOptionalNodes((MappingNode) tuple.getValueNode(), false);
                    }
                }
            }
        }

        private MappingNode cloneMappingNode(MappingNode node, Map<String, String> parameters)
        {
            List<NodeTuple> tuples = new ArrayList<NodeTuple>();
            for (NodeTuple tuple : node.getValue())
            {
                if (tuple.getKeyNode().getNodeId() != scalar)
                {
                    addError(NON_SCALAR_KEY_MESSAGE, tuple.getKeyNode());
                    break;
                }
                Node key = cloneScalarNode((ScalarNode) tuple.getKeyNode(), parameters);
                Node value = cloneNode(tuple.getValueNode(), parameters);
                tuples.add(new NodeTuple(key, value));
            }
            return new MappingNode(node.getTag(), tuples, node.getFlowStyle());
        }

        private Node cloneNode(Node valueNode, Map<String, String> parameters)
        {
            if (valueNode.getNodeId() == mapping)
            {
                return cloneMappingNode((MappingNode) valueNode, parameters);
            }
            else if (valueNode.getNodeId() == sequence)
            {
                return cloneSequenceNode((SequenceNode) valueNode, parameters);
            }
            else if (valueNode.getNodeId() == scalar)
            {
                return cloneScalarNode((ScalarNode) valueNode, parameters);
            }
            addError("unsupported node type: " + valueNode.getNodeId(), valueNode);
            return null;
        }

        private SequenceNode cloneSequenceNode(SequenceNode node, Map<String, String> parameters)
        {
            List<Node> nodes = new ArrayList<Node>();
            for (Node item : node.getValue())
            {
                nodes.add(cloneNode(item, parameters));
            }
            return new SequenceNode(node.getTag(), nodes, node.getFlowStyle());
        }

        private ScalarNode cloneScalarNode(ScalarNode node, Map<String, String> parameters)
        {
            Pattern pattern = Pattern.compile("<<[^>]+>>");
            String value = node.getValue();
            Matcher matcher = pattern.matcher(value);
            StringBuffer sb = new StringBuffer();
            while (matcher.find())
            {
                matcher.appendReplacement(sb, "");
                sb.append(resolveParameter(matcher.group(), parameters, node));
            }
            matcher.appendTail(sb);
            return new ScalarNode(node.getTag(), sb.toString(), node.getStartMark(), node.getEndMark(), node.getStyle());
        }

        private String resolveParameter(String match, Map<String, String> parameters, ScalarNode node)
        {
            String result = "";
            String[] tokens = match.substring(2, match.length() - 2).split("\\|");
            for (String token : tokens)
            {
                token = token.trim();
                if (parameters.containsKey(token))
                {
                    result = parameters.get(token);
                }
                else if (token.startsWith("!"))
                {
                    try
                    {
                        Method method = Inflector.class.getMethod(token.substring(1), String.class);
                        result = (String) method.invoke(null, result);
                    }
                    catch (Exception e)
                    {
                        addError("Invalid parameter function: " + token, node);
                    }
                }
                else
                {
                    addError("Invalid parameter definition: " + match, node);
                }
            }
            return result;
        }

        private MappingNode mergeMappingNodes(MappingNode baseNode, MappingNode templateNode, Class<?> context)
        {

            Map<String, NodeTuple> baseTupleMap = getTupleMap(baseNode);
            for (NodeTuple templateTuple : templateNode.getValue())
            {
                String templateKey = ((ScalarNode) templateTuple.getKeyNode()).getValue();

                if (!nonMergeableFields(context).contains(templateKey))
                {
                    String baseKey = getMatchingKey(baseTupleMap, templateKey);
                    if (baseKey == null)
                    {
                        baseNode.getValue().add(templateTuple);
                    }
                    else
                    {
                        Node keyNode = baseTupleMap.get(baseKey).getKeyNode();
                        if (isOptional(baseKey) && !isOptional(templateKey))
                        {
                            keyNode = templateTuple.getKeyNode();
                        }
                        Node baseInnerNode = baseTupleMap.get(baseKey).getValueNode();
                        Node templateInnerNode = templateTuple.getValueNode();
                        Node valueNode = mergeNodes(baseInnerNode, templateInnerNode, pushMergeContext(context, baseKey));
                        baseNode.getValue().remove(baseTupleMap.get(baseKey));
                        baseNode.getValue().add(new NodeTuple(keyNode, valueNode));
                    }
                }
            }
            return baseNode;
        }

        private Class<?> pushMergeContext(Class<?> context, String key)
        {
            if (context.equals(Resource.class) && isAction(key))
            {
                return Action.class;
            }
            return Object.class;
        }

        private Node mergeNodes(Node baseNode, Node templateNode, Class<?> context)
        {
            if (baseNode.getNodeId() == mapping && templateNode.getNodeId() == mapping)
            {
                return mergeMappingNodes((MappingNode) baseNode, (MappingNode) templateNode, context);
            }
            if (templateNode.getNodeId() == mapping)
            {
                return cleanMergedTuples((MappingNode) templateNode, context);
            }
            return baseNode;
        }

        private MappingNode cleanMergedTuples(MappingNode templateNode, Class<?> context)
        {

            List<NodeTuple> tuples = new ArrayList(templateNode.getValue());
            for (NodeTuple tuple : tuples)
            {
                String key = ((ScalarNode) tuple.getKeyNode()).getValue();
                if (nonMergeableFields(context).contains(key))
                {
                    templateNode.getValue().remove(tuple);
                }
            }
            return templateNode;
        }

        private Set nonMergeableFields(Class<?> element)
        {
            String[] fields = {};
            if (element.equals(Resource.class))
            {
                fields = new String[] {"usage", "summary", "displayName", "type", "is"};
            }
            else if (element.equals(Action.class))
            {
                fields = new String[] {"usage", "summary", "displayName", "is"};
            }
            return new HashSet(Arrays.asList(fields));
        }

        private boolean isAction(String key)
        {
            try
            {
                ActionType.valueOf(normalizeKey(key).toUpperCase());
                return true;
            }
            catch (IllegalArgumentException e)
            {
                return false;
            }
        }

        private boolean isOptional(String key)
        {
            return key.endsWith(OPTIONAL_MODIFIER);
        }

        private String getMatchingKey(Map<String, NodeTuple> tupleMap, String key)
        {
            key = normalizeKey(key);
            for (String resourceKey : tupleMap.keySet())
            {
                if (normalizeKey(resourceKey).equals(key))
                {
                    return resourceKey;
                }
            }
            return null;
        }

        private String normalizeKey(String key)
        {
            if (key.endsWith(OPTIONAL_MODIFIER))
            {
                return key.substring(0, key.length() - 1);
            }
            return key;
        }

        private Map<String, NodeTuple> getTupleMap(MappingNode mappingNode)
        {
            Map<String, NodeTuple> tupleMap = new HashMap<String, NodeTuple>();
            for (NodeTuple tuple : mappingNode.getValue())
            {
                tupleMap.put(((ScalarNode) tuple.getKeyNode()).getValue(), tuple);
            }
            return tupleMap;
        }
    }
}
