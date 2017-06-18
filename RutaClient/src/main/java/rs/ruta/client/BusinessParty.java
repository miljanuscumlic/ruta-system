package rs.ruta.client;

import java.util.*;

import javax.xml.bind.annotation.*;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.PartyType;

@XmlRootElement(name = "BusinessParty", namespace = "urn:rs:ruta:client")
public class BusinessParty extends PartyType implements BusinessPartyInterface
{
	@XmlElement(name = "BusinessPartners")
	private List<PartyType> businessPartners;
	@XmlElement(name = "FollowingPartners")
	private List<PartyType> followingParties;
	@XmlElement(name = "FollowerPartners")
	private List<PartyType> followerParties;

	public BusinessParty() { }

	public List<PartyType> getBusinessPartners()
	{
		if (businessPartners == null)
			businessPartners = new ArrayList<PartyType>();
		return businessPartners;
	}

	public void setBusinessPartners(List<PartyType> businessPartners)
	{
		this.businessPartners = businessPartners;
	}

	public List<PartyType> getFollowingParties()
	{
		if (followingParties == null)
			followingParties = new ArrayList<PartyType>();
		return followingParties;
	}

	public void setFollowingParties(List<PartyType> followingParties)
	{
		this.followingParties = followingParties;
	}

	public List<PartyType> getFollowerParties()
	{
		if (followerParties == null)
			followerParties = new ArrayList<PartyType>();
		return followerParties;
	}

	public void setFollowerParties(List<PartyType> followerParties)
	{
		this.followerParties = followerParties;
	}

}