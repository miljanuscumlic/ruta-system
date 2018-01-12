package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

/**Class containig the list of followerUUIDs of all Parties that follow the {@code Party}.
 *
 */
@XmlRootElement(name = "Followers")
@XmlAccessorType(XmlAccessType.FIELD)
public class Followers
{
	/**UUID of the {@code Party} which followers are listed in this class.
	 */
	@XmlElement(name = "PartyUUID")
	String partyUUID;
	@XmlElement(name = "FollowerUUID")
	List<String> followerUUIDs = null;

	public String getPartyUUID()
	{
		return partyUUID;
	}

	public void setPartyUUID(String partyUUID)
	{
		this.partyUUID = partyUUID;
	}

	public List<String> getFollowerUuids()
	{
		if(followerUUIDs == null)
			followerUUIDs = new ArrayList<>();
		return followerUUIDs;
	}

	public void setFollowerUuids(List<String> uuid)
	{
		this.followerUUIDs = uuid;
	}

	/**Appends followers' ids from the {@code Followers} object to the list.
	 * @param followers
	 */
	public void add(Followers followers)
	{
		getFollowerUuids().addAll(followers.getFollowerUuids());
	}

	/**Append follower's id to the list.
	 * @param uuid
	 */
	public void add(String uuid)
	{
		followerUUIDs.add(uuid);
	}

}
