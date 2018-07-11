package rs.ruta.client.gui;

import java.awt.EventQueue;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemIdentificationType;
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

/**
 * Table model containing data of one product or service represented by an {@link Item} object.
 */
public class ProductTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -9067667667701887210L;
	private static String[] rowNames =
		{
				Messages.getString("ProductTableModel.0"), Messages.getString("ProductTableModel.1"), Messages.getString("ProductTableModel.2"), Messages.getString("ProductTableModel.3"), Messages.getString("ProductTableModel.4"), Messages.getString("ProductTableModel.5"), Messages.getString("ProductTableModel.6"), Messages.getString("ProductTableModel.7"), Messages.getString("ProductTableModel.8"), Messages.getString("ProductTableModel.9")          
		};

	private boolean editable;
	private Item item;
	private boolean changed;
	private MyParty myParty;

	/**
	 * Creates new model for the {@link Item product} table.
	 * @param myParty MyParty object
	 * @param item {@link Item item} to show
	 * @param editable if true, table cells are editable
	 */
	public ProductTableModel(MyParty myParty, Item item, boolean editable)
	{
		super();
		this.item = item;
		this.editable = editable;
		this.myParty = myParty;
		changed = false;
	}

	@Override
	public int getRowCount()
	{
		return item != null ? rowNames.length : 0;
	}

	@Override
	public int getColumnCount()
	{
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(item == null)
			return null;
		if(columnIndex == 0)
			return rowNames[rowIndex];
		try
		{
			switch(rowIndex)
			{
			case 0:
				return item.getNameValue();
			case 1:
				return item.getDescriptionAtIndex(0).getValue();
			case 2:
				return item.getPackSizeNumericValue().toString();
			case 3:
				return item.getSellersItemIdentification().getIDValue();
			case 4:
				return item.getSellersItemIdentification().getBarcodeSymbologyIDValue();
			case 5:
				return item.getCommodityClassification().get(0).getCommodityCodeValue();
			case 6:
				return item.getPrice().getPriceAmountValue().toString();
			case 7:
				return item.getClassifiedTaxCategoryAtIndex(0).getPercentValue().toString();
			case 8:
				return item.getKeywordCount() == 0 ? null :
					item.getKeyword().stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(", ")); 
			case 9:
				return item.isInStock();
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
		//add new product to the list if the cell value is not empty
		if(item == null)
		{
			item = new Item();
			item.setID(UUID.randomUUID().toString());
			final ItemIdentificationType id = new ItemIdentificationType();
			id.setID(myParty.nextProductID());
			item.setSellersItemIdentification(id);
		}
		final Object oldValue = getValueAt(rowIndex, columnIndex);
		try
		{
			String value = null;
			if(obj != null && obj.getClass() == String.class)
				value = ((String) obj).trim();
			switch(rowIndex)
			{
			case 0:
				item.setName(value);
				break;
			case 1:
				item.getDescription().clear();
				item.addDescription(new DescriptionType(value));
				break;
			case 2:
				item.setPackSizeNumeric(BigDecimal.valueOf(Integer.valueOf(value)));
				break;
			case 3:
				break;
			case 4:
				if(item.getSellersItemIdentification() == null)
					throw new ProductException(Messages.getString("ProductTableModel.11")); 
				if(item.getSellersItemIdentification().getBarcodeSymbologyID() == null)
					item.getSellersItemIdentification().setBarcodeSymbologyID(new BarcodeSymbologyIDType());
				item.getSellersItemIdentification().setBarcodeSymbologyID(value);
				break;
			case 5:
				List<CommodityClassificationType> commodities = item.getCommodityClassification();
				if(commodities.isEmpty())
					commodities.add(new CommodityClassificationType());
				if(commodities.get(0).getCommodityCode() == null)
					commodities.get(0).setCommodityCode(new CommodityCodeType());
				commodities.get(0).setCommodityCode(value);
				break;
			case 6:
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
			case 7:
				final List<TaxCategoryType> taxCategoryList = item.getClassifiedTaxCategory();
				final TaxCategoryType newCategory = InstanceFactory.getTaxCategory(value);
				taxCategoryList.clear();
				taxCategoryList.add(newCategory);
				break;
			case 8:
				final List<KeywordType> keywords =
				Stream.of(value.trim().split("( )*[,;]+")). 
				map(keyword -> new KeywordType(keyword)).
				collect(Collectors.toList());
				item.setKeyword(keywords);
				break;
			case 9:
				item.setInStock((boolean) obj);
			default:
				break;
			}
			setChanged(oldValue, getValueAt(rowIndex, columnIndex));
		}
		catch(ProductException e)
		{
			EventQueue.invokeLater(() ->
			{
				JOptionPane.showMessageDialog(null, e.getMessage(), Messages.getString("ProductTableModel.14"), JOptionPane.ERROR_MESSAGE); 
			});
		}
		catch(Exception e)
		{
			EventQueue.invokeLater(() ->
			{
				JOptionPane.showMessageDialog(null, Messages.getString("ProductTableModel.15") + e.getMessage() + Messages.getString("ProductTableModel.16"),  
						Messages.getString("ProductTableModel.17"), JOptionPane.ERROR_MESSAGE); 
			});
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return Object.class;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return columnIndex == 0 ? Messages.getString("ProductTableModel.18") : Messages.getString("ProductTableModel.19");  
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return editable ? (column != 0 && row != 3 ? true : false) : false;
	}

	/**
	 * Sets flag denoting whether the cell value has been changed. The value is considered not being changed
	 * if a new value is equal to an empty string and old value is a {@code null}.
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
	}

	/**
	 * Tests whether the {@link Item} data has changed.
	 * @return
	 */
	public boolean isChanged()
	{
		return changed;
	}

}