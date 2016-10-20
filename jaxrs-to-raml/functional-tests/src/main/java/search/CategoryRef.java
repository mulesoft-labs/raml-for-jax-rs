package search;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CategoryRef {

	public CategoryRef(String id, String name) {
		this.categoryName=name;
		this.categoryId=id;
	}
	public CategoryRef(){
		
	}

	@XmlAttribute
	protected String categoryName;

	@XmlAttribute
	protected String categoryId;
}
