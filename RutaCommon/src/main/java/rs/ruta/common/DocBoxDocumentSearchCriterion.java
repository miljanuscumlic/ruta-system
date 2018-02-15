package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DocBoxDocumentSearchCriterion")
@XmlAccessorType(XmlAccessType.FIELD)
public class DocBoxDocumentSearchCriterion extends SearchCriterion
{
	@XmlElement(name = "PartyID")
	private String partyID;
	@XmlElement(name = "DocumentID")
	private String documentID;

	public String getPartyID()
	{
		return partyID;
	}

	public void setPartyID(String partyID)
	{
		this.partyID = partyID;
	}

	public String getDocumentID()
	{
		return documentID;
	}

	public void setDocumentID(String documentID)
	{
		this.documentID = documentID;
	}
}