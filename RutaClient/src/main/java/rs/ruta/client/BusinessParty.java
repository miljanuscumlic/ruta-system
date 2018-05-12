package rs.ruta.client;

import rs.ruta.common.InstanceFactory;
import java.util.*;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
/**
 * Represents common caracteristics of the party of {@code Ruta application}.
 */
@XmlRootElement(name = "BussinesParty")
@XmlAccessorType(XmlAccessType.NONE)
public class BusinessParty
{
	@XmlElement(name ="Catalogue")
	private Catalogue catalogue;
	@XmlElement(name = "CoreParty")
	private Party coreParty;
	/**
	 * True if the party is followed by MyParty.
	 */
	@XmlElement(name = "Following")
	private boolean following;
	/**
	 * True if the party is a business partner of MyParty.
	 */
	@XmlElement(name = "Partner")
	private boolean partner;
	/**
	 * True if the party is archived.
	 */
	@XmlElement(name = "Archived")
	private boolean archived;
	/**
	 * True if party is deregistered.
	 */
	@XmlElement(name = "Deregistered")
	private boolean deregistered;
	/**
	 * Timestamp of the last received update of this party from the CDR service.
	 */
	@XmlElement(name = "Timestamp")
	private XMLGregorianCalendar timestamp;
	/**
	 * True when there is a recent update for this party from the CDR service.
	 */
	@XmlElement(name = "RecentlyUpdated")
	private boolean recentlyUpdated;

	public BusinessParty()
	{
		coreParty = null;
	}

	@Override
	protected BusinessParty clone()
	{
		BusinessParty bp = new BusinessParty();
		bp.following = following;
		bp.partner = partner;
		bp.archived = archived;
		bp.deregistered = deregistered;
		bp.timestamp = timestamp;
		bp.recentlyUpdated = recentlyUpdated;
		if(coreParty != null)
			bp.coreParty = coreParty.clone();
		if(catalogue != null)
			bp.catalogue = catalogue.clone();
		return bp;
	}

	public XMLGregorianCalendar getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(XMLGregorianCalendar timestamp)
	{
		this.timestamp = timestamp;
	}

	public boolean isRecentlyUpdated()
	{
		return recentlyUpdated;
	}

	public void setRecentlyUpdated(boolean recentlyUpdated)
	{
		this.recentlyUpdated = recentlyUpdated;
	}

	/**
	 * Checks whether the party is followed by MyParty.
	 * @return true if the party is followed by MyParty
	 */
	public boolean isFollowing()
	{
		return following;
	}

/*	//MMM:commented to see whether JAXB is going to complain about it
	public void setFollowing(Boolean following) // Boolean not boolean because of JAXB
	{
		this.following = following;
	}

	public void setPartner(Boolean partner)
	{
		this.partner = partner;
	}*/

	public void setFollowing(boolean following)
	{
		this.following = following;
	}

	/**
	 * Checks whether the party is a business partner of MyParty.
	 * @return true if the party is a business partner of MyParty
	 */
	public boolean isPartner()
	{
		return partner;
	}

	public void setPartner(boolean partner)
	{
		this.partner = partner;
	}

	/**
	 * Checks whether the party is archived.
	 * @return true if the party is archived
	 */
	public boolean isArchived()
	{
		return archived;
	}

	public void setArchived(boolean archived)
	{
		this.archived = archived;
	}

	/**
	 * Checks whether the party is deregistered.
	 * @return true if the party is deregistered
	 */
	public boolean isDeregistered()
	{
		return deregistered;
	}

	public void setDeregistered(boolean deregistered)
	{
		this.deregistered = deregistered;
	}

	public Catalogue getCatalogue()
	{
		return catalogue;
	}

	public void setCatalogue(Catalogue catalogue)
	{
		this.catalogue = catalogue;
	}

	/**
	 * Sets the {@link CatalogueType catalogue} by cloning the passed one.
	 * @param catalogue
	 */
	public void setCatalogue(CatalogueType catalogue)
	{
		if(this.catalogue == null)
			this.catalogue = new Catalogue();
		catalogue.cloneTo(this.catalogue);
	}

	/**
	 * Gets the {@link Party CoreParty} field of the {@link BusinessParty}. If field is equal to a
	 * {@code null} value, it sets new instance of {@code Party} which is get.
	 * @return {@link Party CoreParty} field of the {@link BusinessParty}
	 */
	public Party getCoreParty()
	{
		if(coreParty == null)
			coreParty = new Party();
		return coreParty;
	}

	public void setCoreParty(Party coreParty)
	{
		this.coreParty = coreParty;
	}

	public void setCoreParty(PartyType coreParty)
	{
		this.coreParty = new Party(coreParty);
	}

	/**
	 * Checks whether the {@link Party CoreParty} field of the {@link BusinessParty} is present
	 * i.e not a {@code null} value.
	 * @return true if present, false otherwise
	 */
	public boolean hasCoreParty()
	{
		return coreParty == null ? false : true;
	}

	/**
	 * Gets Party's ID or {@code null} if ID is not set.
	 * @return ID or {@code null} if ID has not been set
	 */
	public String getPartyID()
	{
		return getCoreParty().getPartyID();
	}

	/**
	 * Sets new Party ID by generating a new {@link UUID} identifier.
	 */
	public void setPartyID()
	{
		getCoreParty().setPartyID(UUID.randomUUID().toString());
	}

	/**
	 * Sets Party ID by copying passed identifier.
	 */
	public void setPartyID(String id)
	{
		getCoreParty().setPartyID(id);
	}

	/**
	 * Clears the Party ID by setting {@code List<}{@link PartyIdentificationType}{@code >} to a {@code null} value.
	 * That is the list which elements contain {@link IDType} fields holding the Party ID.
	 */
	public void clearPartyID()
	{
		getCoreParty().setPartyID(null);
	}

	/**
	 * Gets the simple name of the party.
	 * @return party's name
	 */
	public String getPartySimpleName()
	{
		return getCoreParty().getPartySimpleName();
	}

	public void setPartySimpleName(String name)
	{
		getCoreParty().setPartySimpleName(name);
	}

	/**
	 * Returns String representing BussinesParty class. Used as the node name in the tree model.
	 * @return the name of the core Party or null if core Party is not set
	 */
	@Override
	public String toString()
	{
		return InstanceFactory.getPropertyOrNull(coreParty.getPartyName().get(0).getName(), NameType::getValue);
	}

	/**
	 * Checks whether two parties are the same. Check is based on equality of party IDs of theirs core parties.
	 * @param partyOne first {@link BusinessParty party}
	 * @param partyTwo second {@link BusinessParty party}
	 * @return true if parties are the same, false if they are not the same or some party has not set
	 * its party ID.
	 */
	public static boolean sameParties(BusinessParty partyOne, BusinessParty partyTwo)
	{
		return Party.sameParties(partyOne.getCoreParty(), partyTwo.getCoreParty());
	}

	/**
	 * Checks whether two parties are the same. Check is based on equality of party IDs .
	 * @param partyOne first {@link BusinessParty party}
	 * @param partyTwo {@link PartyType core party} of the second party
	 * @return true if parties are the same, false if they are not the same or some party has not set
	 * its party ID.
	 */
	public static boolean sameParties(BusinessParty partyOne, PartyType partyTwo)
	{
		return Party.sameParties(partyOne.getCoreParty(), partyTwo);
	}
}