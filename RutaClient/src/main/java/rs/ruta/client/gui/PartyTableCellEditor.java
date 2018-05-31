package rs.ruta.client.gui;

import java.util.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class PartyTableCellEditor extends DefaultCellEditor
{
	protected String startString;
	protected String endString;

	public PartyTableCellEditor()
	{
		super(new JTextField());
		startString = endString = "";
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent)
	{
		startString = endString = getComponent().getText();
		return super.shouldSelectCell(anEvent);
	}

	@Override
	public boolean stopCellEditing()
	{
		endString = getComponent().getText();
		return super.stopCellEditing();
	}

	@Override
	public JTextField getComponent()
	{
		return (JTextField) super.getComponent();
	}

	/**
	 * Checks if the orderLinesTable cell content has been changed.
	 * @return true if the cell content has been changed
	 */
	public boolean hasChanged()
	{
		boolean diff = !startString.equals(endString);
		endString = startString;
		return diff;
	}
}
