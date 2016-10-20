package search;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Document {
	@XmlAttribute
	public
	String id;
	
	@XmlAttribute
	public
	String url;
	@XmlAttribute
	public
	String title;
	@XmlAttribute
	String richTextAbstract;

	@XmlElement
	public String abstractText;

	@XmlElement
	public String richText;
	
	
	public String imageLink;
	
	@XmlElement(name="category")
	ArrayList<CategoryRef>categories=new ArrayList<CategoryRef>();
	
	public void addCategoryRef(CategoryRef c){
		categories.add(c);
	}
	
	public void addLocationRef(LocationRef l){
		locations.add(l);
	}
	
	@XmlElement(name="location")
	ArrayList<LocationRef>locations= new ArrayList<LocationRef>();
}
