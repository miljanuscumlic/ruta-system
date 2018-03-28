package rs.ruta.client.gui;

import java.awt.EventQueue;
import java.math.BigDecimal;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.*;

import rs.ruta.client.MyParty;
import rs.ruta.client.ProductException;
import rs.ruta.common.datamapper.DetailException;

/**
 *Table model containing data from MyParty.products.
 */
public class ProductTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 3505493863019815517L;
	private static String[] columnNames =
		{
			"No.", "Name", "Description", "Pack Size", "ID", "Barcode", "Commodity Code", "Price", "Tax", "Keywords"
		};

	private MyParty myParty;
	private boolean editable;

	/**
	 * Creates new model for the product table.
	 * @param myParty myParty which products are modeled and shown
	 * @param editable if true, table cells are editable
	 */
	public ProductTableModel(MyParty myParty, boolean editable)
	{
		super();
		this.myParty = myParty;
		this.editable = editable;
	}

	/**
	 * Creates new model for the product table. Party should be set with the subsequent call to
	 * {@link #setParty(MyParty)}.
	 * @param editable if true, table cells are editable
	 * @see ProductTableModel#setMyParty
	 */
	public ProductTableModel(boolean editable)
	{
		super();
		this.myParty = null;
		this.editable = editable;
	}

	public MyParty getParty()
	{
		return myParty;
	}

	@Override
	public int getRowCount()
	{
		return myParty != null ? (editable ? myParty.getProducts().size() + 1 : myParty.getProducts().size()) : 0;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		/*if(myParty == null)
			return null;*/
		if(rowIndex < myParty.getProducts().size())
		{
			switch(columnIndex)
			{
			case 0:
				return rowIndex + 1;
			case 1:
				return myParty.getProductNameAsString(rowIndex);
			case 2:
				return myParty.getProductDescriptionAsString(rowIndex);
			case 3:
				return myParty.getProductPackSize(rowIndex);
			case 4:
				return myParty.getProductIDAsString(rowIndex);
			case 5:
				return myParty.getProductBarcodeAsString(rowIndex);
			case 6:
				return myParty.getProductCommodityCodeAsString(rowIndex);
			case 7:
				return myParty.getProductPrice(rowIndex);
			case 8:
				return myParty.getProductTaxPrecentAsString(rowIndex);
			case 9:
				return myParty.getProductKeywordsAsString(rowIndex);
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
		//add new product to the list if the cell value is not empty
		if(myParty.getProductCount() == rowIndex)
			if(("").equals(((String) obj).trim())) // do nothing
				return;
			else
			{
				try
				{
					myParty.addNewEmptyProduct(rowIndex);
					fireTableRowsInserted(rowIndex + 1, rowIndex + 1);
				}
				catch (DetailException e)
				{
					EventQueue.invokeLater(() ->
						JOptionPane.showMessageDialog(null, "Could not insert new product in the database!",
								"Database Error", JOptionPane.ERROR_MESSAGE)
					);
				}
			}
		try
		{
			switch(columnIndex)
			{
			case 0:
				break;
			case 1:
				myParty.setProductName(rowIndex, (String) obj);
				break;
			case 2:
				myParty.setProductDescription(rowIndex, (String) obj);
				break;
			case 3:
				myParty.setProductPackSizeNumeric(rowIndex, (BigDecimal) obj);
				break;
			case 4:
				myParty.setProductID(rowIndex, obj.toString());
				break;
			case 5:
				myParty.setProductBarcode(rowIndex, obj.toString());
				break;
			case 6:
				myParty.setProductCommodityCode(rowIndex, obj.toString());
				break;
			case 7:
				myParty.setProductPrice(rowIndex, (BigDecimal) obj);
				break;
			case 8:
				myParty.setProductTaxPrecent(rowIndex, obj.toString());
				break;
			case 9:
				myParty.setProductKeywords(rowIndex, obj.toString());
			default:
				break;
			}
		}
		catch(ProductException e)
		{
			EventQueue.invokeLater(() ->
			{
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			});
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		//return getValueAt(0, columnIndex).getClass(); //MMM: check this
		switch(columnIndex)
		{
		case 3:
		case 7:
			return BigDecimal.class;
		case 8:
			return JComboBox.class; //getValueAt(0, columnIndex).getClass();
		default:
			return String.class;
		}
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return columnNames[columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable ? (column != 0 ? true : false) : false;
	}
}