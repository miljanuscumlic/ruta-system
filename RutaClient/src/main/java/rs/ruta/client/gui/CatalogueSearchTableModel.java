package rs.ruta.client.gui;

import java.util.List;
import java.util.stream.Collectors;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PartyType;
import rs.ruta.client.BusinessParty;
import rs.ruta.client.Search;

public class CatalogueSearchTableModel extends SearchTableModel<CatalogueType>
{
	private static final long serialVersionUID = 4685028940868013037L;
	private static String[] columnNames =
		{
			Messages.getString("CatalogueSearchTableModel.0"), Messages.getString("CatalogueSearchTableModel.1"), Messages.getString("CatalogueSearchTableModel.2"), Messages.getString("CatalogueSearchTableModel.3"), Messages.getString("CatalogueSearchTableModel.4"), Messages.getString("CatalogueSearchTableModel.5"), Messages.getString("CatalogueSearchTableModel.6") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
		};
	/**i-th element contains number of catatalogue lines in i-th catalogue of the list.
	 */
	private int[] catalogueLineCount;

	public CatalogueSearchTableModel()
	{
		super();
	}

	public CatalogueSearchTableModel(Search<CatalogueType> catalogues)
	{
		super(catalogues);
		calculateLineCount(catalogues);
	}

	@Override
	public void setSearch(Search<CatalogueType> catalogues)
	{
		super.setSearch(catalogues);
		calculateLineCount(catalogues);
	}

	/**Populate {@code catalogueLineCount} array so that i-th element contains number of catatalogue
	 * lines in i-th catalogue of the {@link Search} object.
	 * @param catalogues {@code Search} object which has the result of the search represented as list of catalogues
	 */
	private void calculateLineCount(Search<CatalogueType> catalogues)
	{
		catalogueLineCount = new int[catalogues.getResultCount()];
		for(int i = 0; i < catalogues.getResultCount(); i++)
			catalogueLineCount[i] = catalogues.getResults().get(i).getCatalogueLineCount();
	}

	@Override
	public int getRowCount()
	{
		if(search == null)
			return 0;
		int rowCount = 0;
		List<CatalogueType> results = search.getResults();
		if(results != null)
			for(CatalogueType cat : results)
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
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		int catalogueNumber = 0;
		int catalogueLineNumber = rowIndex;
		while(catalogueLineNumber >= 0 && catalogueNumber < search.getResultCount())
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
				item.getKeyword().stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(", ")); //$NON-NLS-1$
		case 6:
			return catalogue.getProviderParty() == null ? null :
				catalogue.getProviderParty().getPartyNameAtIndex(0).getNameValue();
		default:
			return null;
		}
	}

	/**
	 * Gets {@link PartyType provider party} for of the {@link ItemType item} with passed index.
	 * @param rowIndex index of the item from the table
	 * @return {@link PartyType provider party} of the item
	 */
	public PartyType getParty(int rowIndex)
	{
		int catalogueNumber = 0;
		int catalogueLineNumber = rowIndex;
		while(catalogueLineNumber >= 0 && catalogueNumber < search.getResultCount())
			catalogueLineNumber -= catalogueLineCount[catalogueNumber++];
//		catalogueLineNumber += catalogueLineCount[--catalogueNumber];
		final CatalogueType catalogue = search.getResults().get(--catalogueNumber);

		return catalogue.getProviderParty();
	}
}