package rs.ruta.common;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.xmldb.api.base.Collection;

import rs.ruta.common.datamapper.DSTransaction;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;

/**
 * Class containig the list of follower UUIDs of all Parties that follow the {@code Party}.
 */
@XmlRootElement(name = "Followers", namespace = "urn:rs:ruta:common")
@XmlType(name = "Followers")
@XmlAccessorType(XmlAccessType.FIELD)
public class Followers
{
	/**
	 * UUID of the {@code Party} which followers are listed in this class.
	 */
	@XmlElement(name = "PartyID")
	private String partyID;
	@XmlElement(name = "FollowerID")
	private List<String> followerIDs = null; //MMM: maybe as a Set

	@Override
	public Followers clone()
	{
		Followers ret = new Followers();
		ret.partyID = partyID;
		ret.followerIDs = new ArrayList<>();
		for(String fID: followerIDs)
			ret.followerIDs.add(fID);
		return ret;
	}

	public String getPartyID()
	{
		return partyID;
	}

	/**
	 * Sets the ID of the {@code Party} which followers are listed in this class.
	 * @param PartyID party ID
	 */
	public void setPartyID(String PartyID)
	{
		this.partyID = PartyID;
	}

	public List<String> getFollowerIDs()
	{
		if(followerIDs == null)
			followerIDs = new ArrayList<>();
		return followerIDs;
	}

	public void setFollowerIDs(List<String> uuid)
	{
		this.followerIDs = uuid;
	}

	/**Appends follower's IDs from the {@code Followers} object to the list of follower's IDs of this .
	 * @param followers
	 */
	public void add(Followers followers)
	{
		getFollowerIDs().addAll(followers.getFollowerIDs());
	}

	/**Appends follower's ID to the list of followers.
	 * @param uuid follower's ID to be added
	 */
	public void add(String uuid)
	{
		getFollowerIDs().add(uuid);
	}

	/**Removes follower's ID from the list of followers.
	 * @param uuid follower's ID to be removed
	 */
	public void remove(String uuid)
	{
		getFollowerIDs().remove(uuid);
	}
}
