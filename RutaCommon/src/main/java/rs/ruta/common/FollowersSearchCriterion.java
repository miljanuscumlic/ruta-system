package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "FollowersSearchCriterion")
@XmlAccessorType(XmlAccessType.FIELD)
public class FollowersSearchCriterion extends SearchCriterion
{
	@XmlElement(name = "FollowerID")
	private String followerID;

	public String getFollowerID()
	{
		return followerID;
	}

	public void setFollowerID(String followerID)
	{
		this.followerID = followerID;
	}
}