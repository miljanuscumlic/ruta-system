package rs.ruta.client.gui;

import java.math.BigDecimal;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.InvoiceLineType;

public class InvoiceLinesTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 19327649202925544L;

	private static String[] columnNames =
		{
				Messages.getString("InvoiceLinesTableModel.0"), Messages.getString("InvoiceLinesTableModel.1"), Messages.getString("InvoiceLinesTableModel.2"), Messages.getString("InvoiceLinesTableModel.3"), Messages.getString("InvoiceLinesTableModel.4"), Messages.getString("InvoiceLinesTableModel.5"), Messages.getString("InvoiceLinesTableModel.6"), Messages.getString("InvoiceLinesTableModel.7"), Messages.getString("InvoiceLinesTableModel.8"), Messages.getString("InvoiceLinesTableModel.9")          
		};
	private static final int ALLOWANCE_INDEX = 10; //MMM to amend
	private List<InvoiceLineType> invoiceLines;
	private boolean editable;

	public InvoiceLinesTableModel(List<InvoiceLineType> invoiceLines, boolean editable)
	{
		super();
		this.invoiceLines = invoiceLines;
		this.editable = editable;
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable ? (column == ALLOWANCE_INDEX ? true : false ) : false;
	}

	@Override
	public int getRowCount()
	{
		return invoiceLines != null ? invoiceLines.size() : 0;
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
		try
		{
			final InvoiceLineType lineItem = invoiceLines.get(rowIndex);

			switch(columnIndex)
			{
			case 0:
				return rowIndex + 1;
			case 1:
				return lineItem.getItem().getNameValue();
			case 2:
				return lineItem.getItem().getPackSizeNumericValue();
			case 3:
				return lineItem.getItem().getSellersItemIdentification().getIDValue();
			case 4:
				return lineItem.getItem().getSellersItemIdentification().getBarcodeSymbologyIDValue();
			case 5:
				return lineItem.getItem().getCommodityClassificationAtIndex(0).getCommodityCodeValue();
			case 6:
				return lineItem.getPrice().getPriceAmountValue();
			case 7:
				return lineItem.getInvoicedQuantityValue();
			case 8:
				return lineItem.getLineExtensionAmountValue();
			case 9:
				return lineItem.getItem().getClassifiedTaxCategoryAtIndex(0).getPercentValue();
			default:
				return null;
			}
		}
		catch(Exception e)
		{
			return null;
		}
	}

	//	@Override
	//	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	//	{
	//		final InvoiceLineType lineItem = invoiceLines.get(rowIndex);
	//
	//		switch(columnIndex)
	//		{
	//		case 9:
	//			lineItem.setLineExtensionAmount((BigDecimal) aValue);
	//			break;
	//		default:
	//			break;
	//		}
	//	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
		case 6: case 7: case 8: case 9:
			return BigDecimal.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

}