package rs.ruta.client;

import javax.swing.table.AbstractTableModel;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;

/**
 *Reading data from BusinessParty.catalog
 */
public class CatalogueTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = -6952612036544033712L;

	private static String[] columnNames =
		{
			"No.", "Name", "Description", "Pack Size", "ID", "Barcode", "Commodity Code", "Keywords"
		};

	private BusinessParty party; //MMM: maybe here should be Catalogue instead of BussinesParty
	private Catalogue catalogue;

	public BusinessParty getParty()
	{
		return party;
	}

	public void setParty(BusinessParty party)
	{
		this.party = party;
		this.catalogue = (Catalogue) party.getCatalogue();
	}

	@Override
	public int getRowCount()
	{
		if(party == null)
			return 0;
		return catalogue != null ? catalogue.getCatalogueLineCount() : 0;
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
//		if(party != null)
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex+1;
			case 1:
//				return party.getProductNameAsString(rowIndex);
				return catalogue.getProductNameAsString(rowIndex);
			case 2:
				return catalogue.getProductDescriptionAsString(rowIndex);
			case 3:
				return catalogue.getProductPackSizeAsBigDecimal(rowIndex);
			case 4:
				return catalogue.getProductIDAsString(rowIndex);
			case 5:
				return catalogue.getProductBarcodeAsString(rowIndex);
			case 6:
				return catalogue.getProductCommodityCodeAsString(rowIndex);
			case 7:
				return catalogue.getProductKeywordsAsString(rowIndex);
			default:
				return null;
			}
		}
//		else
//			return null;
	}
}