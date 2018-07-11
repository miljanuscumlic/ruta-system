package rs.ruta.client.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.Search;

public class SearchListTableModel<T> extends DefaultTableModel
{
	private static final long serialVersionUID = -3491302158739229497L;
	private static String[] colNames = { Messages.getString("SearchListTableModel.0"), Messages.getString("SearchListTableModel.1"), Messages.getString("SearchListTableModel.2"), Messages.getString("SearchListTableModel.3") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private List<Search<T>> searchList;

	public SearchListTableModel()
	{
		super();
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	@Override
	public int getRowCount()
	{
		return searchList != null ? searchList.size() : 0;
	}

	@Override
	public int getColumnCount()
	{
		return colNames.length;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return colNames[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Search<T> search = searchList.get(rowIndex);
		if(searchList.size() == 0)
			return null;
		switch(columnIndex)
		{
		case 0:
			return rowIndex + 1;
		case 1:
			return search.getSearchName();
		case 2:
			return getResultCount(search);
		case 3:
			return search.getTimestampAsString();
		default:
				return null;
		}
	}

	public List<Search<T>> getSearches()
	{
		if(searchList == null)
			searchList = new ArrayList<Search<T>>();
		return searchList;
	}

	public void setSearches(List<Search<T>> searchList)
	{
		this.searchList = searchList;
	}

	private int getResultCount(Search<T> search)
	{
		int resultCount = 0;
		Class<?> resultClazz = search.getResultType();
		if(resultClazz == PartyType.class)
			resultCount = search.getResultCount();
		else if(resultClazz == CatalogueType.class)
		{
			List<T> catalogues = search.getResults();
			for(int i = 0; i < search.getResultCount(); i++)
				resultCount += ((CatalogueType) catalogues.get(i)).getCatalogueLineCount();
		}
		return resultCount;
	}
}
