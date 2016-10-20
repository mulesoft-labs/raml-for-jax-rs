package search;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SearchResult {

	@XmlElement(name="doc")
	protected ArrayList<Document>documents=new ArrayList<Document>();
	
	public ArrayList<Document> getDocuments() {
		return documents;
	}

	@XmlAttribute
	int totalCount;
	
	public int getTotalCountPro() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getOffsetProp() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getCountProp() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@XmlAttribute
	int offset;
	
	@XmlAttribute
	int count;
	
	@XmlAttribute
	String message="Success";
	
	@XmlAttribute
	int errorCode=0;

	public void setErrror(int i, String string) {
		this.errorCode=i;
		this.message=string;
	}
}