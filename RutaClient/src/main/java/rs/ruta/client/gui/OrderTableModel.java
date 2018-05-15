package rs.ruta.client.gui;

import java.math.BigDecimal;

import javax.swing.table.DefaultTableModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.LineItemType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.QuantityType;
import oasis.names.specification.ubl.schema.xsd.order_21.OrderType;

public class OrderTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 19327649202925544L;

	private static String[] columnNames =
		{
				"No.", "Name", "Pack Size", "ID", "Barcode", "Commodity Code", "Price", "Quantity"
		};

	private OrderType order;
	private boolean editable;

	public OrderTableModel(OrderType order, boolean editable)
	{
		super();
		this.order = order;
		this.editable = editable;
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable ? (column == getColumnCount() - 1 ? true : false ) : false;
	}

	public void setOrder(OrderType order)
	{
		this.order = order;
	}

	public OrderType getOrder()
	{
		return order;
	}

	@Override
	public int getRowCount()
	{
		return order != null ? order.getOrderLineCount() : 0;
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
			final LineItemType lineItem = order.getOrderLineAtIndex(rowIndex).getLineItem();

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
				return lineItem.getQuantityValue();
			default:
				return null;
			}
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		final LineItemType lineItem = order.getOrderLineAtIndex(rowIndex).getLineItem();

		switch(columnIndex)
		{
		case 7:
			lineItem.setQuantity((BigDecimal) aValue);
			break;
		default:
			break;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
		case 7:
			return BigDecimal.class;
		default:
			return super.getColumnClass(columnIndex);
		}
	}

}