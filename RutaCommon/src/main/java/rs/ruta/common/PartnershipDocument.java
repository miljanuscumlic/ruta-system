package rs.ruta.common;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class PartnershipDocument
{
	@XmlElement(name = "ID")
	private IDType id;
	@XmlElement(name = "RequesterParty")
	private PartyType requesterParty;
	@XmlElement(name = "RequestedParty")
	private PartyType requestedParty;
	@XmlElement(name = "IssueTime")
	XMLGregorianCalendar issueTime;
	@XmlElement(name = "DocumentReference") //MMM not used
	DocumentReference documentReference;

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

	/**
	 * Gets the name of the requester party or {@code null} if it is not set.
	 * @return
	 */
	public String getRequesterPartyName()
	{
		try
		{
			return requesterParty.getPartyNameAtIndex(0).getNameValue();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	/**
	 * Gets the name of the requester party or {@code null} if it is not set.
	 * @return
	 */
	public String getRequestedPartyName()
	{
		try
		{
			return requestedParty.getPartyNameAtIndex(0).getNameValue();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	/**
	 * Gets the ID of the requester party or {@code null} if it is not set.
	 * @return
	 */
	public String getRequesterPartyID()
	{
		try
		{
			return requesterParty.getPartyIdentificationAtIndex(0).getIDValue();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	/**
	 * Gets the ID of the requester party or {@code null} if it is not set.
	 * @return
	 */
	public String getRequestedPartyID()
	{
		try
		{
			return requestedParty.getPartyIdentificationAtIndex(0).getIDValue();
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public XMLGregorianCalendar getIssueTime()
	{
		return issueTime;
	}

	/**
	 * Sets the time when the request has been sent by the sender Party.
	 * @param issueTime
	 */
	public void setIssueTime(XMLGregorianCalendar issueTime)
	{
		this.issueTime = issueTime;
	}

	public DocumentReference getDocumentReference()
	{
		return documentReference;
	}

	public void setDocumentReference(DocumentReference documentReference)
	{
		this.documentReference = documentReference;
	}
}