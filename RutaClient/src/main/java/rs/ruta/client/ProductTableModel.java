package rs.ruta.client;

import javax.swing.table.*;

public class ProductTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 3505493863019815517L;
	private BusinessParty businessParty ;

	public ProductTableModel(BusinessParty businessParty)
	{
		this.businessParty = businessParty;
		businessParty.importMyProducts();
	}

	public BusinessParty getBusinessParty()
	{
		return businessParty;
	}

	public void setBusinessParty(BusinessParty businessParty)
	{
		this.businessParty = businessParty;
	}

	@Override
	public int getRowCount()
	{
		return businessParty.getMyProducts().size() + 1;
	}

	@Override
	public int getColumnCount()
	{
		return 8; // MMM: change to reflect the number of fields in the ItemType class
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex < businessParty.getMyProducts().size())
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex+1;
			case 1:
				return businessParty.getProductName(rowIndex);
			case 2:
				return businessParty.getProductDescription(rowIndex);
			case 3:
				return businessParty.getProductPackSizeNumeric(rowIndex);
			case 4:
				return businessParty.getProductID(rowIndex);
			case 5:
				return businessParty.getProductBarcode(rowIndex);
			case 6:
				return businessParty.getProductCommodityCode(rowIndex);
			case 7:
				return businessParty.getProductItemClassificationCode(rowIndex);
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
		if(businessParty.getProductCount() == rowIndex)
			businessParty.addNewEmptyProduct();
		switch(columnIndex)
		{
		case 0:
			break;
		case 1:
			businessParty.setProductName(rowIndex, (String) obj);
			break;
		case 2:
			businessParty.setProductDescription(rowIndex, (String) obj);
			break;
		case 3:
			businessParty.setProductPackSizeNumeric(rowIndex, obj.toString());
			break;
		case 4:
			businessParty.setProductID(rowIndex, obj.toString());
			break;
		case 5:
			businessParty.setProductBarcode(rowIndex, obj.toString());
			break;
		case 6:
			businessParty.setProductCommodityCode(rowIndex, obj.toString());
			break;
		case 7:
			businessParty.setProductItemClassificationCode(rowIndex, obj.toString());
		default:
			;
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