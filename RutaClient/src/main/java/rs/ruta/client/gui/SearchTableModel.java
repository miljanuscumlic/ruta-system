package rs.ruta.client.gui;

import javax.swing.table.DefaultTableModel;

import rs.ruta.client.Search;

public abstract class SearchTableModel<T> extends DefaultTableModel
{
	private static final long serialVersionUID = -472598908596227041L;
	protected Search<T> search;

	public SearchTableModel()
	{
		super();
	}

	public SearchTableModel(Search<T> results)
	{
		super();
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
		return false;
	}
}
