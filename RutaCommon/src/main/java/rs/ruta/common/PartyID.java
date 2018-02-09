package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "PartyID", namespace = "urn:rs:ruta:services")
@XmlAccessorType(XmlAccessType.FIELD)
public class PartyID
{
	@XmlElement(name = "DocumentID")
	private String documentID;

	public PartyID() {}

	public PartyID(String id)
	{
		documentID = id;
	}

	public String getPartyID() { return documentID; }

	public void setPartyID(String partyID) { this.documentID = partyID; }

}
