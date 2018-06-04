package rs.ruta.client.gui;

import javax.swing.table.DefaultTableModel;

import rs.ruta.client.Catalogue;

/**
 * Data model for a table displaying {@link Catalogue} of a party.
 */
public class CatalogueTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -6952612036544033712L;

	private static String[] columnNames =
		{
				"No.", "Name", "Description", "Pack Size", "ID", "Barcode", "Commodity Code", "Price", "Tax", "Keywords"
		};

	private Catalogue catalogue;

	public CatalogueTableModel()
	{
		super();
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	public void setCatalogue(Catalogue catalogue)
	{
		this.catalogue = catalogue;
	}

	@Override
	public int getRowCount()
	{
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
		switch(columnIndex)
		{
		case 0:
			return rowIndex + 1;
		case 1:
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
			return catalogue.getProductPrice(rowIndex);
		case 8:
			return catalogue.getProductTaxPrecentAsString(rowIndex);
		case 9:
			return catalogue.getProductKeywordsAsString(rowIndex);
		default:
			return null;
		}
	}
}