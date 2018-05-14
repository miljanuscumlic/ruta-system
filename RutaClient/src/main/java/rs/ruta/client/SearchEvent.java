package rs.ruta.client;

import java.awt.event.ActionEvent;

public class SearchEvent extends ActionEvent
{
	private static final long serialVersionUID = -4405562652384954954L; // MMM check this below
	public static final String PARTY_SEARCH_ADDED = "Party search added";  //maybe one string for search added is enough
	public static final String PARTY_SEARCH_REMOVED = "Party search removed";
	public static final String PARTY_SEARCH_UPDATED = "Party search updated"; //maybe one string for search updated is enough
	public static final String CATALOGUE_SEARCH_ADDED = "Catalogue search added"; //maybe one string for search added is enough
	public static final String CATALOGUE_SEARCH_REMOVED = "Catalogue search removed";
	public static final String CATALOGUE_SEARCH_UPDATED = "Catalogue search updated"; //maybe one string for search updated is enough
	public static final String ALL_PARTY_SEARCHES_REMOVED = "All party searches removed";
	public static final String ALL_CATALOGUE_SEARCHES_REMOVED = "All catalogue searches removed";

	public SearchEvent(Object source, String command)
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
	public SearchEvent(Object source, int id, String command)
	{
		super(source, id, command);
	}
}