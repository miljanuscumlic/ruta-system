package rs.ruta.client;

import java.awt.EventQueue;
import java.math.BigDecimal;

import javax.swing.JOptionPane;
import javax.swing.table.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.KeywordType;
import rs.ruta.common.datamapper.DetailException;

/**
 *Table model containing data from MyParty.products.
 */
public class ProductTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 3505493863019815517L;
	private static String[] columnNames =
		{
			"No.", "Name", "Description", "Pack Size", "ID", "Barcode", "Commodity Code", "Keywords"
		};


	private MyParty party;
	private boolean editable;

	/**Creates new model for the product table.
	 * @param party party which products are modeled and shown
	 * @param editable if true, table cells are editable
	 */
	public ProductTableModel(MyParty party, boolean editable)
	{
		this.party = party;
		this.editable = editable;
	}

	/**Creates new model for the product table. Party should be set with the subsequent call to
	 * setMyParty(MyParty party)
	 * @param editable if true, table cells are editable
	 * @see ProductTableModel#setMyParty
	 */
	public ProductTableModel(boolean editable)
	{
		this.party = null;
		this.editable = editable;
	}

	public MyParty getParty()
	{
		return party;
	}

	/**Sets the Party for the product table model.
	 * @param party Party that is going to be set
	 */
	public void setParty(MyParty party)
	{
		this.party = party;
	}

	@Override
	public int getRowCount()
	{
		return party != null ? (editable ? party.getProducts().size() + 1 : party.getProducts().size()) : 0;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		/*if(party == null)
			return null;*/
		if(rowIndex < party.getProducts().size())
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
				return party.getProductPackSize(rowIndex);
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
		else
			return null;
	}

	@Override
	public void setValueAt(Object obj, int rowIndex, int columnIndex)
	{
		//add new product to the list if the cell value not empty
		if(party.getProductCount() == rowIndex)
			if(("").equals(((String) obj).trim())) // do nothing
				return;
			else
			{
				try
				{
					party.addNewEmptyProduct(rowIndex);
					fireTableRowsInserted(rowIndex+1, columnIndex);
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
				party.setProductName(rowIndex, (String) obj);
				break;
			case 2:
				party.setProductDescription(rowIndex, (String) obj);
				break;
			case 3:
				party.setProductPackSizeNumeric(rowIndex, (BigDecimal) obj);
				break;
			case 4:
				party.setProductID(rowIndex, obj.toString());
				break;
			case 5:
				party.setProductBarcode(rowIndex, obj.toString());
				break;
			case 6:
				party.setProductCommodityCode(rowIndex, obj.toString());
				break;
			case 7:
				party.setProductKeywords(rowIndex, obj.toString());
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
		return columnNames[columnIndex];
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable ? (column != 0 ? true : false) : false;
	}
}