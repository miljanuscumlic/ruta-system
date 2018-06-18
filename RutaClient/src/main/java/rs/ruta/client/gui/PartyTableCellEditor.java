package rs.ruta.client.gui;

import java.util.*;

import javax.swing.*;

public class PartyTableCellEditor extends DefaultCellEditor
{
	private static final long serialVersionUID = -300615440639687513L;
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
	 * Checks whether the table cell content has been changed.
	 * @return true if the cell content has been changed
	 */
	public boolean hasChanged()
	{
		boolean diff = !startString.equals(endString);
		endString = startString;
		return diff;
	}
}