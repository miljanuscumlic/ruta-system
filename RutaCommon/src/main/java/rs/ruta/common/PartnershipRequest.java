package rs.ruta.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(name = "PartnershipRequest")
@XmlType(name = "PartnershipRequest")
public class PartnershipRequest extends PartnershipDocument
{
	@XmlElement(name = "Inbound")
	private boolean inbound;
	@XmlElement(name = "Resolved")
	private boolean resolved;
	@XmlElement(name = "Accepted")
	private boolean accepted;
	@XmlElement(name = "ResponsedTime")
	XMLGregorianCalendar responsedTime;

	/**
	 * Gets value of the flag denoting whether the request is received or sent.
	 * @return true if it received; false if sent
	 */
	public boolean isInbound()
	{
		return inbound;
	}

	/**
	 * Sets value of the flag denoting whether the request is received or sent.
	 * @param inbound true if it received; false if sent
	 */
	public void setInbound(boolean inbound)
	{
		this.inbound = inbound;
	}

	/**
	 * Gets value of the flag denoting whether the request is resolved or not.
	 * @return true if resolved
	 */
	public boolean isResolved()
	{
		return resolved;
	}

	/**
	 * Sets flag denoting whether the request is resolved or not.
	 * @param resolved true if resolved
	 */
	public void setResolved(boolean resolved)
	{
		this.resolved = resolved;
	}

	/**
	 * Gets value of the flag denoting request's accepted.
	 * @return true if request is accepted; false means rejection or that it is not yet
	 * {@link #resolved}.
	 */
	public boolean isAccepted()
	{
		return accepted;
	}

	/**
	 * Sets the flag denoting request's accepted.
	 * @return true if request is accepted; false means rejection or that it is not yet
	 * {@link #resolved}.
	 */
	public void setAccepted(boolean resolution)
	{
		this.accepted = resolution;
	}

	/**
	 * Gets the time when the response to this request has been created by the correspondent Party.
	 */
	public XMLGregorianCalendar getResponsedTime()
	{
		return responsedTime;
	}

	/**
	 * Sets the time when the response to this request has been created by the correspondent Party.
	 * @param responsedTime
	 */
	public void setResponsedTime(XMLGregorianCalendar receivedTime)
	{
		this.responsedTime = receivedTime;
	}

	@Override
	public String toString()
	{
		return inbound ? getRequesterPartyName() : getRequestedPartyName();
	}

}