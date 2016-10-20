package search;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocationRef {

	public LocationRef() {
	}
	
	
	@XmlAttribute
	protected String locationName;
	
	public LocationRef(String locationName, String locationId) {
		super();
		this.locationName = locationName;
		this.locationId = locationId;
	}


	@XmlAttribute
	protected String locationId;
}
