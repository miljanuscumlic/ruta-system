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

	private BusinessParty party; //MMM: maybe here should be CatalogueType instead of BussinesParty

	public BusinessParty getParty()
	{
		return party;
	}

	public void setParty(BusinessParty party)
	{
		this.party = party;
	}

	@Override
	public int getRowCount()
	{
		if(party == null)
			return 0;
		CatalogueType catalogue = party.getCatalogue();
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
				return party.getProductNameAsString(rowIndex);
			case 2:
				return party.getProductDescriptionAsString(rowIndex);
			case 3:
				return party.getProductPackSizeAsBigDecimal(rowIndex);
			case 4:
				return party.getProductIDAsString(rowIndex);
			case 5:
				return party.getProductBarcodeAsString(rowIndex);
			case 6:
				return party.getProductCommodityCodeAsString(rowIndex);
			case 7:
				return party.getProductKeywordsAsString(rowIndex);
			default:
				return null;
			}
		}
//		else
//			return null;
	}
}