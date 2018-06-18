package rs.ruta.client.gui;

import java.awt.event.ActionEvent;

import rs.ruta.client.Party;
import rs.ruta.client.Item;

/**
 * Class that describes the addition, change or removal of a {@link Item} objects from the data model.
 */
public class ItemEvent extends ActionEvent
{
	private static final long serialVersionUID = -4405562652384954954L;
	public static final String ITEM_ADDED = "Item added";
	public static final String ITEM_REMOVED = "Item removed";
	public static final String ITEM_UPDATED = "Item updated";

	public ItemEvent(Object source, String command)
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
	public ItemEvent(Object source, int id, String command)
	{
		super(source, id, command);
	}

}