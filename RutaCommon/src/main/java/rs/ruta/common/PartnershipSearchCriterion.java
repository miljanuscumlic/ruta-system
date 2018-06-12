package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PartnershipSearchCriterion")
@XmlAccessorType(XmlAccessType.NONE)
public class PartnershipSearchCriterion extends SearchCriterion
{
	@XmlElement(name = "RequesterPartyID")
	private String requesterPartyID;
	@XmlElement(name = "RequestedPartyID")
	private String requestedPartyID;

	public String getRequesterPartyID()
	{
		return requesterPartyID;
	}
	public void setRequesterPartyID(String requesterPartyID)
	{
		this.requesterPartyID = requesterPartyID;
	}
	public String getRequestedPartyID()
	{
		return requestedPartyID;
	}
	public void setRequestedPartyID(String requestedPartyID)
	{
		this.requestedPartyID = requestedPartyID;
	}

}