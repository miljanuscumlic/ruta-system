package rs.ruta.common;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class BusinessPartnershipDocument
{
	@XmlElement(name = "ID")
	private IDType id;
	@XmlElement(name = "RequesterParty")
	private PartyType requesterParty;
	@XmlElement(name = "RequestedParty")
	private PartyType requestedParty;

	public IDType getID()
	{
		return id;
	}

	public String getIDValue()
	{
		return id != null ? id.getValue() : null;
	}

	public void setID(IDType id)
	{
		this.id = id;
	}

	/**
	 * Sets a new value for an {@link IDType ID} field. If ID is {@code null} it creates a new
	 * {@link IDType ID} object.
	 * @param value value to set
	 * @return {@link IDType ID} object
	 */
	public IDType setID(@Nullable final String value)
	{
		if(id == null)
			id = new IDType(value);
		id.setValue(value);
		return id;
	}

	public PartyType getRequesterParty()
	{
		return requesterParty;
	}

	public void setRequesterParty(PartyType requesterParty)
	{
		this.requesterParty = requesterParty;
	}

	public PartyType getRequestedParty()
	{
		return requestedParty;
	}

	public void setRequestedParty(PartyType requestedParty)
	{
		this.requestedParty = requestedParty;
	}

}