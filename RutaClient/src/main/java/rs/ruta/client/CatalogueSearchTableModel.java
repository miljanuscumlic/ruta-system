package rs.ruta.client;

import java.util.stream.Collectors;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;

public class CatalogueSearchTableModel extends SearchTableModel<CatalogueType>
{
	private static final long serialVersionUID = 4685028940868013037L;
	private static String[] columnNames =
		{
			"No.", "Name", "Description", "Barcode", "Commodity Code", "Keywords", "Party"
		};
	private int[] catalogueLineCount; // i-th element contains number of cat. lines in i-th catalogue of the list

	public CatalogueSearchTableModel(boolean editable)
	{
		super(editable);
	}

	public CatalogueSearchTableModel(Search<CatalogueType> catalogues, boolean editable)
	{
		super(catalogues, editable);
		calculateLineCount(catalogues);
	}

	@Override
	public void setSearch(Search<CatalogueType> catalogues)
	{
		super.setSearch(catalogues);
		calculateLineCount(catalogues);
	}

	/**
	 * @param catalogues
	 */
	private void calculateLineCount(Search<CatalogueType> catalogues)
	{
		catalogueLineCount = new int[catalogues.size()];
		for(int i = 0; i < catalogues.size(); i++)
			catalogueLineCount[i] = catalogues.getResults().get(i).getCatalogueLineCount();
	}

	@Override
	public int getRowCount()
	{
		int rowCount = 0;
		for(CatalogueType cat : search.getResults())
			rowCount += cat.getCatalogueLineCount();
		return rowCount;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return columnNames[columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		int catalogueNumber = 0;
		int catalogueLineNumber = rowIndex;
		while(catalogueLineNumber >= 0 && catalogueNumber < search.size())
			catalogueLineNumber -= catalogueLineCount[catalogueNumber++];
		catalogueLineNumber += catalogueLineCount[--catalogueNumber];
		CatalogueType catalogue = search.getResults().get(catalogueNumber);
		ItemType item = catalogue.getCatalogueLineAtIndex(catalogueLineNumber).getItem();
		switch(columnIndex)
		{
		case 0:
			return rowIndex + 1;
		case 1:
			return item.getNameValue();
		case 2:
			return item.getDescriptionCount() == 0 ? null : item.getDescriptionAtIndex(0).getValue();
		case 3:
			return item.getSellersItemIdentification() == null ? null :
				item.getSellersItemIdentification().getBarcodeSymbologyIDValue();
		case 4:
			return item.getCommodityClassificationCount() == 0 ? null : item.getCommodityClassificationAtIndex(0).getCommodityCodeValue();
		case 5:
			return item.getKeywordCount() == 0 ? null :
				item.getKeyword().stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(" ,"));
		case 6:
			return catalogue.getProviderParty() == null ? null :
				catalogue.getProviderParty().getPartyNameAtIndex(0).getNameValue();
		default:
			return null;
		}
	}
}