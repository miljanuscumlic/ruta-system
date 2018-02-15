package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "DocBoxAllIDsSearchCriterion")
@XmlAccessorType(XmlAccessType.FIELD)
public class DocBoxAllIDsSearchCriterion extends SearchCriterion
{
	@XmlElement(name = "PartyID")
	private String partyID;

	public String getPartyID()
	{
		return partyID;
	}

	public void setPartyID(String partyID)
	{
		this.partyID = partyID;
	}

}