package rs.ruta.client.gui;

import java.awt.event.ActionEvent;

import rs.ruta.client.Party;
import rs.ruta.common.PartnershipDocument;

/**
 * Class that describes the addition, change or removal of a {@link PartnershipDocument} objects from the data model.
 */
public class PartnershipEvent extends ActionEvent
{
	private static final long serialVersionUID = -4405562652384954954L;
	public static final String OUTBOUND_PARTNERSHIP_REQUEST_ADDED = "Outbound Partnership request added";
	public static final String OUTBOUND_PARTNERSHIP_REQUEST_REMOVED = "Outbound Partnership request removed";
	public static final String OUTBOUND_PARTNERSHIP_REQUEST_UPDATED = "Outbound Partnership request updated";
	public static final String INBOUND_PARTNERSHIP_REQUEST_ADDED = "Inbound Partnership request added";
	public static final String INBOUND_PARTNERSHIP_REQUEST_REMOVED = "Inbound Partnership request removed";
	public static final String INBOUND_PARTNERSHIP_REQUEST_UPDATED = "Inbound Partnership request updated";

	public PartnershipEvent(Object source, String command)
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
	public PartnershipEvent(Object source, int id, String command)
	{
		super(source, id, command);
	}

}