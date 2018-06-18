package rs.ruta.client.gui;

import java.awt.EventQueue;
import java.math.BigDecimal;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rs.ruta.client.MyParty;
import rs.ruta.client.ProductException;
import rs.ruta.common.datamapper.DetailException;

/**
 * Table model containing data of MyParty products and services.
 */
public class ProductListTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 3505493863019815517L;
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	private static String[] columnNames =
		{
			"No.", "Name", "Description", "Pack Size", "ID", "Barcode", "Commodity Code", "Price", "Tax precent", "Keywords"
		};

	private MyParty myParty;
	private boolean editable;

	/**
	 * Creates new model for the product table.
	 * @param myParty myParty which products are modeled and shown
	 * @param editable if true, table cells are editable
	 */
	public ProductListTableModel(MyParty myParty, boolean editable)
	{
		super();
		this.myParty = myParty;
		this.editable = editable;
	}

	@Override
	public int getRowCount()
	{
		return myParty != null && myParty.getProducts() != null ? myParty.getProducts().size() : 0;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
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

	@Override
	public void setValueAt(Object obj, int rowIndex, int columnIndex)
	{
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
				final String newID = obj.toString();
				if(!newID.equals(myParty.getProductIDAsString(rowIndex)))
					myParty.setProductID(rowIndex, newID);
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
			logger.error("Coud not set the property. Exception is: ", e);
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
		return editable ? (column != 0 && column != 4 ? true : false) : false;
	}
}