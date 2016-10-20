package search;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CategoryInfo {

	@XmlAttribute(name="name")
	
	protected String nameProp;
	@XmlAttribute(name="id")
	protected String idProp;
	
	@XmlElement(name="description")
	protected String descriptionProp;
	
	public String getName() {
		return nameProp;
	}

	public void setName(String name) {
		this.nameProp = name;
	}

	public String getId() {
		return idProp;
	}

	public void setId(String id) {
		this.idProp = id;
	}

	public String getDescription() {
		return descriptionProp;
	}

	public void setDescription(String description) {
		this.descriptionProp = description;
	}

	@XmlElement(name="categories")
	ArrayList<CategoryInfo>childrenProp=new ArrayList<CategoryInfo>();

	public ArrayList<CategoryInfo> getChildren() {
		return childrenProp;
	}

	public void setChildren(ArrayList<CategoryInfo> children) {
		this.childrenProp = children;
	}

	public CategoryInfo find(String category) {
		if (nameProp!=null&&nameProp.equals(category)){
			return this;
		}
		if (idProp!=null&&idProp.equals(category)){
			return this;
		}
		for (CategoryInfo q:childrenProp){
			CategoryInfo find = q.find(category);
			if (find!=null){
				return find;
			}
		}
		return null;
	}
}
