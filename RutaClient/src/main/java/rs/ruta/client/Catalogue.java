package rs.ruta.client;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.CatalogueLineType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemLocationQuantityType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.ItemType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.PriceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.TaxCategoryType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.BarcodeSymbologyIDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.CommodityCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.KeywordType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PackSizeNumericType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.PriceAmountType;
import rs.ruta.common.InstanceFactory;

/**
 * Wrapper class for {@link CatalogueType} with additional convinient methods.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Catalogue")
public class Catalogue extends CatalogueType
{
	private static final long serialVersionUID = -1147808285332382380L;

	public Catalogue() { super(); }

	/**
	 * Copy constructor that copies passed {@link CatalogueType} superclass object and retrieves new
	 * {@code Catalogue} object.
	 * @param catalogue {@code CatalogueType} object with which new {@code Catalogue} is initialized
	 */
	public Catalogue(CatalogueType catalogue)
	{
		catalogue.cloneTo(this);
	}

	@Override
	public Catalogue clone()
	{
		Catalogue ret = new Catalogue();
		super.cloneTo(ret);
		return ret;
	}


	public String getProductNameAsString(int index)
	{
		try
		{
			ItemType item = getCatalogueLineAtIndex(index).getItem();
			return InstanceFactory.getPropertyOrNull(item.getName(), NameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public String getProductDescriptionAsString(int index)
	{
		try
		{
			ItemType item = getCatalogueLineAtIndex(index).getItem();
			return InstanceFactory.getPropertyOrNull(item.getDescription().get(0), DescriptionType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public String getProductIDAsString(int index)
	{
		try
		{
			ItemType item = getCatalogueLineAtIndex(index).getItem();
			return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getID(), IDType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public String getProductBarcodeAsString(int index)
	{
		try
		{
			ItemType item = getCatalogueLineAtIndex(index).getItem();
			return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getBarcodeSymbologyID(), BarcodeSymbologyIDType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public BigDecimal getProductPackSizeAsBigDecimal(int index) //MMM: check whether return value should be String instead
	{
		try
		{
			ItemType item = getCatalogueLineAtIndex(index).getItem();
			return InstanceFactory.getPropertyOrNull(item.getPackSizeNumeric(), PackSizeNumericType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public String getProductCommodityCodeAsString(int index)
	{
		try
		{
			ItemType item = getCatalogueLineAtIndex(index).getItem();
			return InstanceFactory.getPropertyOrNull(item.getCommodityClassification().get(0).getCommodityCode(),
					CommodityCodeType::getValue);
		}
		catch(Exception e) { return null; }
	}

	/**
	 * Retrieves the list of keywords set for the {@link ItemType}.
	 * @param index index of the {@code ItemType} in the {@link Catalogue}
	 * @return list of keywords
	 */
	public String getProductKeywordsAsString(int index)
	{
		try
		{
			ItemType item = getCatalogueLineAtIndex(index).getItem();
			return item.getKeywordCount() == 0 ? null :
				item.getKeyword().stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(", "));
		}
		catch(Exception e) { return null; }
	}

	public List<KeywordType> getProductKeywords(int index)
	{
		try
		{
			ItemType item = getCatalogueLineAtIndex(index).getItem();
			return item.getKeyword();
		}
		catch(Exception e) { return null; }
	}

	public BigDecimal getProductPrice(final int index)
	{
		try
		{
			final CatalogueLineType catLine = getCatalogueLineAtIndex(index);
			return InstanceFactory.getPropertyOrNull(catLine.getRequiredItemLocationQuantityAtIndex(0).getPrice().getPriceAmount(),
					PriceAmountType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public String getProductTaxPrecentAsString(final int index)
	{
		try
		{
			final ItemType item = getCatalogueLineAtIndex(index).getItem();
			return InstanceFactory.getPropertyOrNull(item.getClassifiedTaxCategoryAtIndex(0), TaxCategoryType::getPercentValue).toString();
		}
		catch(Exception e) { return null; }
	}

	/**
	 * Adds new empty {@link ItemType} to the {@link Catalogue}.
	 */
	public void addNewEmptyProduct()
	{
		CatalogueLineType catalogueLine = new CatalogueLineType();
		catalogueLine.setItem(new ItemType());
		addCatalogueLine(catalogueLine);
	}

	/**
	 * Returns the number of catalogue's items.
	 * @return number of catalogue's items
	 */
	public int getProductCount() // method is superfluous
	{
		return getCatalogueLineCount();
	}

}
