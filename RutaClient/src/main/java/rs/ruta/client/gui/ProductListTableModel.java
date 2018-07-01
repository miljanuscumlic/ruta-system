package rs.ruta.client.gui;

import java.awt.EventQueue;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.BarcodeSymbologyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CommodityCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.KeywordType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PriceAmountType;
import rs.ruta.client.Item;
import rs.ruta.client.MyParty;
import rs.ruta.client.ProductException;
import rs.ruta.common.InstanceFactory;
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
			"No.", "Name", "Description", "Pack Size", "ID", "Barcode", "Commodity Code", "Price", "Tax", "Keywords"
		};

	private List<Item> items;
	private boolean editable;
	private boolean changed;
	private MyParty myParty;

	/**
	 * Creates new model for the product table.
	 * @param myParty myParty which products are modeled and shown
	 * @param editable if true, table cells are editable
	 */
	public ProductListTableModel(List<Item> items, MyParty myParty, boolean editable)
	{
		super();
		this.items = items;
		this.myParty = myParty;
		this.editable = editable;
	}

	@Override
	public int getRowCount()
	{
		return items != null ? items.size() : 0;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		try
		{
			final Item item = items.get(rowIndex);
			switch(columnIndex)
			{
			case 0:
				return rowIndex + 1;
			case 1:
				return item.getNameValue();
			case 2:
				return item.getDescriptionAtIndex(0).getValue();
			case 3:
				return item.getPackSizeNumericValue().toString();
			case 4:
				return item.getSellersItemIdentification().getIDValue();
			case 5:
				return item.getSellersItemIdentification().getBarcodeSymbologyIDValue();
			case 6:
				return item.getCommodityClassification().get(0).getCommodityCodeValue();
			case 7:
				return item.getPrice().getPriceAmountValue().toString();
			case 8:
				return item.getClassifiedTaxCategoryAtIndex(0).getPercentValue().toString();
			case 9:
				return item.getKeywordCount() == 0 ? null :
					item.getKeyword().stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(", "));
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
	public void setValueAt(Object obj, int rowIndex, int columnIndex)
	{
		final Item item = items.get(rowIndex);
		final Object oldValue = getValueAt(rowIndex, columnIndex);
		try
		{
			final String value = ((String) obj).trim();
			switch(columnIndex)
			{
			case 0:
				break;
			case 1:
				item.setName(value);
				break;
			case 2:
				item.getDescription().clear();
				item.addDescription(new DescriptionType(value));
				break;
			case 3:
				item.setPackSizeNumeric(BigDecimal.valueOf(Integer.valueOf(value)));
				break;
			case 4:
				break;
			case 5:
				if(item.getSellersItemIdentification() == null)
					throw new ProductException("Product ID is mandatory, and it must be entered first!");
				if(item.getSellersItemIdentification().getBarcodeSymbologyID() == null)
					item.getSellersItemIdentification().setBarcodeSymbologyID(new BarcodeSymbologyIDType());
				item.getSellersItemIdentification().setBarcodeSymbologyID(value);
				break;
			case 6:
				List<CommodityClassificationType> commodities = item.getCommodityClassification();
				if(commodities.isEmpty())
					commodities.add(new CommodityClassificationType());
				if(commodities.get(0).getCommodityCode() == null)
					commodities.get(0).setCommodityCode(new CommodityCodeType());
				commodities.get(0).setCommodityCode(value);
				break;
			case 7:
				if(item.getPrice() == null)
					item.setPrice(new PriceType());
				if(item.getPrice().getPriceAmount() == null)
					item.getPrice().setPriceAmount(new PriceAmountType());
				// to conform to the UBL, currencyID is mandatory
				final PriceAmountType priceAmount = item.getPrice().getPriceAmount();
				priceAmount.setCurrencyID("RSD"); // MMM: currencyID should be pooled from somewhere in the UBL definitions - check specifications
				priceAmount.setValue(BigDecimal.valueOf(Double.valueOf(value)));
				item.getPrice().setPriceAmount(priceAmount);
				break;
			case 8:
				final List<TaxCategoryType> taxCategoryList = item.getClassifiedTaxCategory();
				final TaxCategoryType newCategory = InstanceFactory.getTaxCategory(value);
				taxCategoryList.clear();
				taxCategoryList.add(newCategory);
				break;
			case 9:
				final List<KeywordType> keywords =
				Stream.of(value.trim().split("( )*[,;]+")).
				map(keyword -> new KeywordType(keyword)).
				collect(Collectors.toList());
				item.setKeyword(keywords);
			default:
				break;
			}
			if(item.isInStock())
				setChanged(oldValue, getValueAt(rowIndex, columnIndex));
			fireTableDataChanged(); // so that table could be sorted on update
		}
		catch(ProductException e)
		{
			EventQueue.invokeLater(() ->
			{
				JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			});
		}
		catch(Exception e)
		{
			EventQueue.invokeLater(() ->
			{
				JOptionPane.showMessageDialog(null, "Invalid field format. " + e.getMessage() + "\nReverting to the previous value.",
						"Error", JOptionPane.ERROR_MESSAGE);
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
		return editable ? (column != 0 && column != 4 ? true : false) : false;
	}


	/**
	 * Sets flag denoting whether the cell value has been changed. The value is considered not being changed
	 * if a new value is equal to an empty string when old value was a {@code null}. This method sets
	 * {@code MyParty#dirtyCatalogue} if value has changed.
	 * @param oldOne old value of the cell
	 * @param newOne new value of the cell
	 * @return true if values differs, false otherwise
	 */
	private <T> void setChanged(T oldOne, T newOne)
	{
		if(newOne != null)
		{
			if(newOne instanceof String && newOne.toString().equals("") && oldOne == null)
				changed = changed || false;
			changed = changed || !newOne.equals(oldOne);
		}
		if(changed)
		{
			myParty.setDirtyCatalogue(true);
			changed = false;
		}
	}

	/**
	 * Tests whether the {@link Item} data has changed.
	 * @return
	 */
	public boolean isChanged()
	{
		return changed;
	}

	public List<Item> getProducts()
	{
		return items;
	}

	public void setProducts(List<Item> items)
	{
		this.items = items;
	}

}