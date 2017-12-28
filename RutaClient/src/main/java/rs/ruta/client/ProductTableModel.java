package rs.ruta.client;

import java.math.BigDecimal;

import javax.swing.table.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.KeywordType;

public class ProductTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 3505493863019815517L;
	private BusinessParty businessParty;
	private boolean editable;

	/**Creates new model for the product table.
	 * @param businessParty party which products are modeled and shown
	 * @param editable if true, table cells are editable
	 */
	public ProductTableModel(BusinessParty businessParty, boolean editable)
	{
		this.businessParty = businessParty;
		this.editable = editable;
	}

	/**Creates new model for the product table. Party should be set with the subsequent call to
	 * setBusinessParty(BusinessParty businessParty)
	 * @param editable if true, table cells are editable
	 * @see ProductTableModel#setBusinessParty
	 */
	public ProductTableModel( boolean editable)
	{
		this.businessParty = null;
		this.editable = editable;
	}

	public BusinessParty getBusinessParty()
	{
		return businessParty;
	}

	/**Sets the Party for the product table model.
	 * @param businessParty Party that is going to be set
	 */
	public void setBusinessParty(BusinessParty businessParty)
	{
		this.businessParty = businessParty;
	}

	@Override
	public int getRowCount()
	{
		return businessParty != null ? (editable ? businessParty.getProducts().size() + 1 : businessParty.getProducts().size()) : 0;
	}

	@Override
	public int getColumnCount()
	{
		return 8;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex < businessParty.getProducts().size())
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex+1;
			case 1:
				return businessParty.getProductNameAsString(rowIndex);
			case 2:
				return businessParty.getProductDescriptionAsString(rowIndex);
			case 3:
				return businessParty.getProductPackSizeAsBigDecimal(rowIndex);
			case 4:
				return businessParty.getProductIDAsString(rowIndex);
			case 5:
				return businessParty.getProductBarcodeAsString(rowIndex);
			case 6:
				return businessParty.getProductCommodityCodeAsString(rowIndex);
			case 7:
				return businessParty.getProductKeywordsAsString(rowIndex);
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
		//add new product to the list if the cell value not empty
		if(businessParty.getProductCount() == rowIndex)
			if(("").equals((String) obj)) // do nothing
				return;
			else
			{
				businessParty.addNewEmptyProduct();
				fireTableRowsInserted(rowIndex+1, columnIndex);
			}
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
			businessParty.setProductPackSizeNumeric(rowIndex, (BigDecimal) obj);
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
			businessParty.setProductKeywords(rowIndex, obj.toString());
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
		case 3:
			return BigDecimal.class;
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
			return "Keywords";
		default:
			return "0";
		}
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable ? (column != 0 ? true : false) : false;
	}

}