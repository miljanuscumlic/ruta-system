package rs.ruta.client.gui;

import java.awt.event.ActionEvent;

import rs.ruta.client.BusinessParty;
import rs.ruta.client.Party;

/**
 * Class that describes the addition, change or removal of a {@link BusinessParty} objects from the data model.
 */
public class BusinessPartyEvent extends ActionEvent
{
	private static final long serialVersionUID = -4405562652384954954L;
	public static final String BUSINESS_PARTNER_ADDED = "Business Partner added";
	public static final String BUSINESS_PARTNER_REMOVED = "Business Partner removed";
	public static final String BUSINESS_PARTNER_TRANSFERED = "Business Partner transfered";
	public static final String OTHER_PARTY_ADDED = "Other Party added";
	public static final String OTHER_PARTY_REMOVED = "Other Party removed";
	public static final String OTHER_PARTY_TRANSFERED = "Other Party transfered";
	public static final String ARCHIVED_PARTY_ADDED = "Archived Party added";
	public static final String ARCHIVED_PARTY_REMOVED = "Archived Party removed";
//	public static final String DEREGISTERED_PARTY_ADDED = "Deregistered Party added";
//	public static final String DEREGISTERED_PARTY_REMOVED = "Deregistered Party removed";
	public static final String MY_FOLLOWING_PARTY_ADDED = "My Following Party added";
	public static final String MY_FOLLOWING_PARTY_REMOVED = "My Following Party removed";
	public static final String ALL_PARTIES_REMOVED = "All lists removed";
	public static final String ARCHIVED_LIST_REMOVED = "Archived list removed";
	public static final String BUSINESS_LIST_REMOVED = "Business Partners list removed";
	public static final String OTHER_LIST_REMOVED = "Other parties list removed";
//	public static final String DEREGISTERED_LIST_REMOVED = "Deregistered list removed";
	public static final String PARTY_UPDATED = "Party updated";
	public static final String PARTY_MOVED = "Party moved from one list to another";
	public static final String CATALOGUE_UPDATED = "Party's catalogue updated";

	public BusinessPartyEvent(Object source, String command)
	{
		this(source, ActionEvent.ACTION_PERFORMED, command);
	}

	/**
	 * Creates new {@link ActionEvent} object describing change in a {@link Party} object in the data model.
	 * @param source The object that originated the event
	 * @param uuid An integer that identifies the event. Allowable values are,ActionEvent.ACTION_PERFORMED,
	 * ITEM_STATE_CHANGED, ADJUSTMENT_VALUE_CHANGED, TEXT_VALUE_CHANGED
	 * @param command A string that may specify a command (possibly one of several) associated with the event
	 */
	public BusinessPartyEvent(Object source, int id, String command)
	{
		super(source, id, command);
	}

}