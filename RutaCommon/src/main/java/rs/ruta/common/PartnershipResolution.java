package rs.ruta.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlRootElement(name = "PartnershipResolution")
@XmlType(name = "PartnershipResolution")
public class PartnershipResolution extends PartnershipDocument
{
	@XmlElement(name = "Accepted")
	boolean accepted;
	@XmlElement(name = "ResponsedTime")
	XMLGregorianCalendar responsedTime;

	public boolean isAccepted()
	{
		return accepted;
	}

	public void setAccepted(boolean resolution)
	{
		this.accepted = resolution;
	}

	public XMLGregorianCalendar getResponsedTime()
	{
		return responsedTime;
	}

	/**
	 * Sets the time when the request has been received by the receiver Party.
	 * @param responsedTime
	 */
	public void setResponsedTime(XMLGregorianCalendar receivedTime)
	{
		this.responsedTime = receivedTime;
	}

}
