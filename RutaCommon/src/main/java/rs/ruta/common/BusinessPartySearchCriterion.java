package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "BusinessPartySearchCriterion")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessPartySearchCriterion extends SearchCriterion
{
	@XmlElement(name = "Following")
	private boolean following;
	@XmlElement(name = "Partner")
	private boolean partner;
	@XmlElement(name = "Other")
	private boolean other;
	@XmlElement(name = "Archived")
	private boolean archived;
	@XmlElement(name = "Deregistered")
	private boolean deregistered;

	public boolean isFollowing()
	{
		return following;
	}
	public void setFollowing(boolean following)
	{
		this.following = following;
	}
	public boolean isPartner()
	{
		return partner;
	}
	public void setPartner(boolean partner)
	{
		this.partner = partner;
	}
	public boolean isOther()
	{
		return other;
	}
	public void setOther(boolean other)
	{
		this.other = other;
	}
	public boolean isArchived()
	{
		return archived;
	}
	public void setArchived(boolean archived)
	{
		this.archived = archived;
	}
	public boolean isDeregistered()
	{
		return deregistered;
	}
	public void setDeregistered(boolean deregistered)
	{
		this.deregistered = deregistered;
	}

}