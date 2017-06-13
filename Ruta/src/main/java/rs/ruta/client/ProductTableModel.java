package rs.ruta.client;

import java.util.*;
import javax.swing.table.*;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ItemType;

public class ProductTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 3505493863019815517L;
	private ArrayList<ItemType> clientProducts;
	private Client client;

	public ProductTableModel(Client client)
	{
		this.client = client;
		clientProducts = client.getMyProducts();
		if(client.getMyProducts() == null)
			client.importMyProducts();
	}

	@Override
	public int getRowCount()
	{
		return clientProducts.size() + 1;
	}

	@Override
	public int getColumnCount()
	{
		return 8; // MMM: change to reflect the number of fields in the ItemType class
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex < clientProducts.size())
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex+1;
			case 1:
				return client.getProductName(rowIndex);
			case 2:
				return client.getProductDescription(rowIndex);
			case 3:
				return client.getProductPackSizeNumeric(rowIndex);
			case 4:
				return client.getProductID(rowIndex);
			case 5:
				return client.getProductBarcode(rowIndex);
			case 6:
				return client.getProductCommodityCode(rowIndex);
			case 7:
				return client.getProductItemClassificationCode(rowIndex);
			default:
				return null;
			}
		}
		else
			return null;
	}

	@Override
	public void setValueAt(Object obj, int rowIndex, int columnIndex)
	{
//		if(! obj.toString().equals("")) // actual change of the cell content
		{
			if(client.getProductCount() == rowIndex)
				client.addNewEmptyProduct();
			switch(columnIndex)
			{
			case 0:
				break;
			case 1:
				client.setProductName(rowIndex, (String) obj);
				break;
			case 2:
				client.setProductDescription(rowIndex, (String) obj);
				break;
			case 3:
				client.setProductPackSizeNumeric(rowIndex, obj.toString());
				break;
			case 4:
				client.setProductID(rowIndex, obj.toString());
				break;
			case 5:
				client.setProductBarcode(rowIndex, obj.toString());
				break;
			case 6:
				client.setProductCommodityCode(rowIndex, obj.toString());
				break;
			case 7:
				client.setProductItemClassificationCode(rowIndex, obj.toString());
			default:
				;
			}
		}

//		System.out.println(p);
//		System.out.println(clientProducts);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return String.class;
		case 1:
			return String.class;
		default:
			return String.class;
		}
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
		case 0:
			return "No.";
		case 1:
			return "Name";
		case 2:
			return "Description";
		case 3:
			return "Pack Size";
		case 4:
			return "ID";
		case 5:
			return "Barcode";
		case 6:
			return "Commodity Code";
		case 7:
			return "Classification Code";
		default:
			return "0";
		}
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return column != 0 ? true : false;
	}

}