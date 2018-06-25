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
 * Class containig the list of UUIDs of all Parties that are some kind of associates of one particular
 * {@code Party}. For example, associates could be followers or parties that the document should be sent to.
 */
@XmlRootElement(name = "Associates")
@XmlType(name = "Associates")
@XmlAccessorType(XmlAccessType.NONE)
public class Associates
{
	/**
	 * UUID of the {@code Party} which associates are listed in this class.
	 */
	@XmlElement(name = "PartyID")
	private String partyID;
	@XmlElement(name = "AssociateID")
	private List<String> associateIDs = null; //MMM: maybe as a Set

	@Override
	public Associates clone()
	{
		Associates ret = new Associates();
		ret.partyID = partyID;
		ret.associateIDs = new ArrayList<>();
		for(String fID: associateIDs)
			ret.associateIDs.add(fID);
		return ret;
	}

	public String getPartyID()
	{
		return partyID;
	}

	/**
	 * Sets the ID of the {@code Party} which associates are listed in this class.
	 * @param PartyID party ID
	 */
	public void setPartyID(String PartyID)
	{
		this.partyID = PartyID;
	}

	public List<String> getAssociateIDs()
	{
		if(associateIDs == null)
			associateIDs = new ArrayList<>();
		return associateIDs;
	}

	public void setAssociateIDs(List<String> uuid)
	{
		this.associateIDs = uuid;
	}

	/**
	 * Gets the {@code Party ID} of the associate from the list at the index.
	 * @param index index of a Party
	 * @return Party ID
	 * @throws IndexOutOfBoundsException if index is invalid
	 */
	public String getAssociateAtIndex(int index)
	{
		return associateIDs.get(index);
	}

	/**
	 * Appends associate's IDs from the {@code Associates} object to the list of associate's IDs of this.
	 * @param associates
	 */
	public void addAllAssociates(Associates associates)
	{
		getAssociateIDs().addAll(associates.getAssociateIDs());
	}

	/**
	 * Appends associate's ID to the list of all associates.
	 * @param uuid associate's ID to be added
	 */
	public void addAssociate(String uuid)
	{
		getAssociateIDs().add(uuid);
	}

	/**
	 * Removes associate's ID from the list of all associates.
	 * @param uuid associate's ID to be removed
	 */
	public void removeAssociate(String uuid)
	{
		getAssociateIDs().remove(uuid);
	}

	/**
	 * Checks whether party is associate.
	 * @param uuid party's ID to check
	 * @return true if it is associate; false otherwise
	 */
	public boolean isAssociate(String uuid)
	{
		return getAssociateIDs().stream().anyMatch(associateID -> associateID.equals(uuid));
	}
}