package org.raml.parser.visitor;

import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.model.Raml2;
import org.raml.model.Resource;
import org.raml.model.ResourceType;
import org.raml.model.TraitModel;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.loader.ClassPathResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * <p>PreservingTemplatesBuilder class.</p>
 *
 * @author kor
 * @version $Id: $Id
 */
public final class PreservingTemplatesBuilder extends RamlDocumentBuilder {
	
	
	/** {@inheritDoc} */
	public void onCustomTagError(Tag tag, Node node, String message) {
		if (IncludeResolver.INCLUDE_TAG.equals(tag))
        {
			if ( message.startsWith("Include file is empty")){
				return;
			}
            throw new RuntimeException("resource not found: " + ((ScalarNode) node).getValue());
        }		
	}
	
	/**
	 * <p>getTemplateResolver.</p>
	 *
	 * @return a {@link org.raml.parser.visitor.TemplateResolver} object.
	 */
	public TemplateResolver getTemplateResolver()
    {
        if (templateResolver == null)
        {
            templateResolver = new TemplateResolver(getResourceLoader(), this,false);
        }
        return templateResolver;
    }
	
	private final class IncludedResourceOrTraitBuilder<T> extends
			YamlDocumentBuilder<T> {
		private IncludedResourceOrTraitBuilder(Class<T> documentClass,
				ResourceLoader resourceLoader, TagResolver[] tagResolvers) {
			super(documentClass, resourceLoader, tagResolvers);
		}
		
	    public void onMappingNodeStart(MappingNode mappingNode, TupleType tupleType)
	    {
	        super.onMappingNodeStart(mappingNode, tupleType);
	        if (getDocumentContext().peek() instanceof Resource)
	        {
	            Resource resource = (Resource) getDocumentContext().peek();
	            getTemplateResolver().resolve(mappingNode, resource.getRelativeUri(), resource.getUri());
	        }
	        else if (isBodyBuilder(getBuilderContext().peek()))
	        {
	            getMediaTypeResolver().resolve(mappingNode);
	        }
	    }
		private boolean isBodyBuilder(NodeBuilder builder)
	    {
	        try
	        {
	            Field valueType = builder.getClass().getDeclaredField("valueClass");
	            valueType.setAccessible(true);
	            return valueType.get(builder) != null && ((Class) valueType.get(builder)).getName().equals("org.raml.model.MimeType");
	        }
	        catch (NoSuchFieldException e)
	        {
	            return false;
	        }
	        catch (IllegalAccessException e)
	        {
	            return false;
	        }
	    }

		
		protected void preBuildProcess() {
			getTemplateResolver().init(getRootNode());
			getMediaTypeResolver().beforeDocumentStart(getRootNode());
		}
	}

	protected TagResolver[] rs;
	
	/**
	 * <p>Constructor for PreservingTemplatesBuilder.</p>
	 */
	public PreservingTemplatesBuilder() {
		super(Raml2.class, new ClassPathResourceLoader(), new TagResolver[]{});
		this.rs = new TagResolver[0];
	}

	/**
	 * <p>Constructor for PreservingTemplatesBuilder.</p>
	 *
	 * @param resourceLoader a {@link org.raml.parser.loader.ResourceLoader} object.
	 * @param tagResolvers an array of {@link org.raml.parser.tagresolver.TagResolver} objects.
	 */
	public PreservingTemplatesBuilder(ResourceLoader resourceLoader,
			TagResolver[] tagResolvers) {
		super(Raml2.class, resourceLoader, tagResolvers);
		this.rs = tagResolvers;
	}
	protected HashMap<String, Exception>errorMap=new HashMap<String, Exception>();
	

	/**
	 * <p>Getter for the field <code>errorMap</code>.</p>
	 *
	 * @return a {@link java.util.HashMap} object.
	 */
	public HashMap<String, Exception> getErrorMap() {
		return errorMap;
	}

	
	/**
	 * <p>build.</p>
	 *
	 * @param content a {@link java.io.Reader} object.
	 * @return a {@link org.raml.model.Raml2} object.
	 */
	public Raml2 build(Reader content) {
		LinkedHashMap<String, ResourceType> resourceTypes = new LinkedHashMap<String, ResourceType>();
		LinkedHashMap<String, TraitModel> traits = new LinkedHashMap<String, TraitModel>();
		Raml2 build = (Raml2) super.build(content);
		Map<String, MappingNode> resourceTypesMap = getTemplateResolver()
				.getResourceTypesMap();
		
		for (String s : resourceTypesMap.keySet()) {
			try{
			MappingNode z = resourceTypesMap.get(s);
			IncludedResourceOrTraitBuilder<ResourceType> includedResourceOrTraitBuilder = new IncludedResourceOrTraitBuilder<ResourceType>(
					ResourceType.class, getResourceLoader(),
					new TagResolver[] { new IncludeResolver() });
			ResourceType partialType = includedResourceOrTraitBuilder.build(z);
			partialType.setRelativeUri(s);
			resourceTypes.put(s, partialType);
			}catch (Exception e) {
				errorMap.put(s, e);
			}
		}
		resourceTypesMap = getTemplateResolver().getTraitsMap();
		for (String s : resourceTypesMap.keySet()) {
			MappingNode z = resourceTypesMap.get(s);
			IncludedResourceOrTraitBuilder<TraitModel> includedResourceOrTraitBuilder = new IncludedResourceOrTraitBuilder<TraitModel>(
					TraitModel.class, getResourceLoader(),
					new TagResolver[] { new IncludeResolver() });
			
			TraitModel partialType = includedResourceOrTraitBuilder.build(z);

			traits.put(s, partialType);
		}
		build.setResourceTypesModel(resourceTypes);
		build.setTraitsModel(traits);
		return build;
	}
}
