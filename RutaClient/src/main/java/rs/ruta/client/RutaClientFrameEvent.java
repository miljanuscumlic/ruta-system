package rs.ruta.client;

import java.awt.event.ActionEvent;

import rs.ruta.client.gui.RutaClientFrame;

/**
 * Class that describes updates of the view of a {@link RutaClientFrame}.
 */
public class RutaClientFrameEvent extends ActionEvent
{
	private static final long serialVersionUID = -4405562652384954954L;
	//MMM not used - could be deleted if is obsolete and also the whole RutaClientFrameEvent class
	public static final String SELECT_NEXT = "Select next node in a tree";

	public RutaClientFrameEvent(Object source, String command)
	{
		this(source, ActionEvent.ACTION_PERFORMED, command);
	}

	/**
	 * Creates new {@link ActionEvent} object describing change in the data model.
	 * @param source The object that originated the event
	 * @param uuid An integer that identifies the event. Allowable values are: ActionEvent.ACTION_PERFORMED,
	 * ITEM_STATE_CHANGED, ADJUSTMENT_VALUE_CHANGED, TEXT_VALUE_CHANGED
	 * @param command A string that may specify a command (possibly one of several) associated with the event
	 */
	public RutaClientFrameEvent(Object source, int id, String command)
	{
		super(source, id, command);
	}

}