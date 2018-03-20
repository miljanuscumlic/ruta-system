package rs.ruta.client.gui;

import javax.swing.table.AbstractTableModel;

import rs.ruta.client.Search;

public abstract class SearchTableModel<T> extends AbstractTableModel
{
	private static final long serialVersionUID = -472598908596227041L;
	protected boolean editable;
	protected Search<T> search;

	public SearchTableModel(boolean editable)
	{
		this.editable = editable;
	}

	public SearchTableModel(Search<T> results, boolean editable)
	{
		this.editable = editable;
		this.search = results;
	}

	public void setSearch(Search<T> results)
	{
		this.search = results;
	}

	@Override
	public int getRowCount()
	{
		return search != null ? search.getResultCount() : 0;
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable;
	}
}
