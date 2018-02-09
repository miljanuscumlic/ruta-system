package rs.ruta.client;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.table.AbstractTableModel;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;

public class SearchListTableModel<T> extends AbstractTableModel
{
	private static final long serialVersionUID = -3491302158739229497L;
	private static String[] colNames = { "No.", "Name", "Number of results", "Time" };
	private List<Search<T>> searchList;

	@Override
	public int getRowCount()
	{
		return searchList.size();
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
		Class<?> resultClazz = search.getResultClass();
		if(resultClazz == PartyType.class)
			resultCount = search.getResultCount();
		else if(resultClazz == CatalogueType.class)
		{
			List<T> catalogues = search.getResults();
			for(int i = 0; i < search.size(); i++)
				resultCount += ((CatalogueType) catalogues.get(i)).getCatalogueLineCount();
		}
		return resultCount;
	}
}
