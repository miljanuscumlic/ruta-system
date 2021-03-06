package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "PartyID")
@XmlAccessorType(XmlAccessType.NONE)
public class PartyID
{
	@XmlElement(name = "DocumentID")
	private String documentID;
//	@XmlTransient
	private String partyID;

	public PartyID() {}

	public PartyID(String partyID, String documentID)
	{
		this.partyID = partyID;
		this.documentID = documentID;
	}

	public String getDocumentID()
	{
		return documentID;
	}

	public void setDocumentID(String documentID)
	{
		this.documentID = documentID;
	}

	public String getPartyID()
	{
		return partyID;
	}

	public void setPartyID(String partyID)
	{
		this.partyID = partyID;
	}

/*	public String getPartyID() { return documentID; }

	public void setPartyID(String partyID) { this.documentID = partyID; }*/

}
