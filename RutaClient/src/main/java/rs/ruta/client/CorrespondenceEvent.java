package rs.ruta.client;

import java.awt.event.ActionEvent;

import rs.ruta.client.gui.CorrespondenceTreeModel;

/**
 * Class that describes updates of the {@link CorrespondenceTreeModel}.
 */
public class CorrespondenceEvent extends ActionEvent
{
	private static final long serialVersionUID = -6038373567746479191L;
	public static final String CORRESPONDENCE_ADDED = "Correspondence added";
	public static final String CORRESPONDENCE_REMOVED = "Correspondence removed";
	public static final String CORRESPONDENCE_UPDATED = "Correspondence updated";

	public CorrespondenceEvent(Object source, String command)
	{
		this(source, ActionEvent.ACTION_PERFORMED, command);
	}

	/**
	 * Creates new {@link ActionEvent} object describing change in a {@link Correspondence} object in the data model.
	 * @param source The object that originated the event.
	 * @param uuid An integer that identifies the event. Allowable values are: ActionEvent.ACTION_PERFORMED,
	 * ITEM_STATE_CHANGED, ADJUSTMENT_VALUE_CHANGED, TEXT_VALUE_CHANGED
	 * @param command A string that may specify a command (possibly one of several) associated with the event
	 */
	public CorrespondenceEvent(Object source, int id, String command)
	{
		super(source, id, command);
	}
}