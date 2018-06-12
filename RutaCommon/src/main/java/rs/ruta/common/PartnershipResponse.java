package rs.ruta.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "PartnershipResponse")
@XmlType(name = "PartnershipResponse")
public class PartnershipResponse extends PartnershipDocument
{
	@XmlElement(name = "Response")
	boolean accepted;

	/**
	 * Gets value of the flag denoting whether the request is accepted or not.
	 * @return true if accepted
	 */
	public boolean isAccepted()
	{
		return accepted;
	}

	/**
	 * Sets flag denoting whether the request is accepted or not.
	 * @param resolved true if accepted
	 */
	public void setAccepted(boolean accepted)
	{
		this.accepted = accepted;
	}

}