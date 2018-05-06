package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;

@XmlRootElement(name = "DeregistrationNotice")
@XmlType(name = "DeregistrationNotice")
@XmlAccessorType(XmlAccessType.NONE)
public class DeregistrationNotice
{
	@XmlElement(name = "Party")
	private PartyType party;

	public DeregistrationNotice() { }

	public DeregistrationNotice(PartyType party)
	{
		this.party = party;
	}

	public PartyType getParty()
	{
		return party;
	}

	public void setParty(PartyType party)
	{
		this.party = party;
	}

/*	@XmlElement(name = "PartyID")
	private String partyID;

	public DeregistrationNotice(String partyID)
	{
		this.partyID = partyID;
	}

	public String getPartyID()
	{
		return partyID;
	}

	public void setPartyID(String partyID)
	{
		this.partyID = partyID;
	}*/

}