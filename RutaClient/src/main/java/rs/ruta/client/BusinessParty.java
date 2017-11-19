package rs.ruta.client;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import rs.ruta.client.datamapper.*;

//@XmlRootElement(name = "BussinesParty", namespace = "urn:rs:ruta:client:businessparty") //commented because it is not root xml element anymore, MyParty is instead
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessParty
{
//	@XmlTransient
	@XmlElement(name = "MyProduct")
	private ArrayList<ItemType> products; //database alternative - MMM: to be replaced with the real database
	@XmlTransient
	private ItemTypeBinaryFileMapper<ItemType> itemDataMapper;
	@XmlElement(name = "CoreParty")
//	@XmlTransient
	private Party coreParty;
	@XmlElement(name = "Following")
//	@XmlTransient
	private boolean following; // MMM: ?
	@XmlElement(name = "Partner")
//	@XmlTransient
	private boolean partner; //MMM: ?

	public BusinessParty()
	{
		products = getMyProducts();
		coreParty = null;
	}

	public boolean isFollowing()
	{
		return following;
	}

	public void setFollowing(Boolean following) // Boolean not boolean because of JAXB
	{
		this.following = following;
	}

	public boolean isPartner()
	{
		return partner;
	}

	public void setPartner(Boolean partner)
	{
		this.partner = partner;
	}

	public Party getCoreParty()
	{
		if(coreParty == null)
			coreParty = new Party();
		return coreParty;
	}

	public void setCoreParty(Party coreParty)
	{
		this.coreParty = coreParty;
	}

	public boolean hasCoreParty()
	{
		return coreParty == null ? false : true;
	}

	public ArrayList<ItemType> getMyProducts()
	{
		if (products == null)
			products = new ArrayList<ItemType>();
		return products;
	}

	public void setProducts(ArrayList<ItemType> products)
	{
		this.products = products;
	}

	public void setProducts(CatalogueType catalogue)
	{
/*		try
		{
			String strID = InstanceFactory.getPropertyOrNull(catalogue.getID(), IDType::getValue);
			catalogueID = Integer.parseInt(strID);
		}
		catch(Exception e)
		{
			catalogueID = 0;
		}*/
		List<CatalogueLineType> catalogueLines = catalogue.getCatalogueLine();
		if(catalogueLines.size() != 0)
		{
			products.clear();
			for(CatalogueLineType line : catalogueLines)
				products.add(line.getItem());
		}
	}

	public void setItemDataMapper(ItemTypeBinaryFileMapper<ItemType> itemDataMapper)
	{
		this.itemDataMapper = itemDataMapper;
	}

	public void setItemDataMapper(String fileStore)
	{
		this.itemDataMapper = new ItemTypeBinaryFileMapper<ItemType>(this, fileStore);
	}


	public String getProductNameAsString(int index)
	{
		ItemType item = products.get(index);
		/*NameType name = item.getName();
		if(name != null)
			return name.getValue();
		else
			return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getName(), NameType::getValue);
		}
		catch(Exception e) { return null; }

	}

	public String getProductDescriptionAsString(int index)
	{
		ItemType item = products.get(index);
		/*List<DescriptionType> descriptions = item.getDescription();
		if(descriptions.size() != 0)
		{
			DescriptionType desc = descriptions.get(0);
			if(desc != null)
				return desc.getValue();
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getDescription().get(0), DescriptionType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public String getProductIDAsString(int index)
	{
		ItemType item = products.get(index);
/*		ItemIdentificationType identification = item.getSellersItemIdentification();
		if(identification != null)
		{
			IDType id = identification.getID();
			if(id != null)
				return id.getValue();
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getID(), IDType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public String getProductBarcodeAsString(int index)
	{
		ItemType item = products.get(index);
		/*ItemIdentificationType identification = item.getSellersItemIdentification();
		if(identification != null)
		{
			BarcodeSymbologyIDType barcode = identification.getBarcodeSymbologyID();
			if(barcode != null)
				return barcode.getValue();
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getBarcodeSymbologyID(), BarcodeSymbologyIDType::getValue);
		}
		catch(Exception e) { return null; }

	}

	public BigDecimal getProductPackSizeAsBigDecimal(int index) //MMM: check if return value should be String instead
	{
		ItemType item = products.get(index);
		/*	PackSizeNumericType packSize = item.getPackSizeNumeric();
		if(packSize != null)
			return packSize.toString();
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getPackSizeNumeric(), PackSizeNumericType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public String getProductCommodityCodeAsString(int index)
	{
		ItemType item = products.get(index);
		/*List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() != 0)
		{
			CommodityClassificationType commodityClass = commodities.get(0);
			if(commodityClass != null)
			{
				CommodityCodeType commodityCode = commodityClass.getCommodityCode();
				if(commodityCode != null)
					return commodityCode.toString();
			}
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getCommodityClassification().get(0).getCommodityCode(), CommodityCodeType::getValue);
		}
		catch(Exception e) { return null; }
	}

	/**Retrieves the list of keywords set for the {@link ItemType}
	 * @param index index of the {@code ItemType} in the list of products
	 * @return list of keywords
	 */
	public String getProductKeywordsAsString(int index)
	{
		ItemType item = products.get(index);
		return item.getKeywordCount() == 0 ? null :
			item.getKeyword().stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(" ,"));
	}

	public List<KeywordType> getProductKeywords(int index)
	{
		ItemType item = products.get(index);
		return item.getKeyword();
	}

	public void addNewEmptyProduct()
	{
//		myProducts.add(InstanceFactory.newInstanceItemType());
		products.add(new ItemType());
	}

	/**Returns the number of party's products.
	 * @return number of products
	 */
	public int getProductCount()
	{
		return products.size();
	}

	/**
	 * Reads all products of party from the data store. If the products are already read, skips the read.
	 */
	public void importMyProducts()
	{
		if(itemDataMapper != null)
		{
			if(products.size() == 0)
			{
				products = (ArrayList<ItemType>) itemDataMapper.findAll();
				if(products == null) // nothing found
					products = new ArrayList<ItemType>();
			}
		}
	}

	/**
	 * Writes all Products to the data store.
	 */
	public void exportMyProducts()
	{
		itemDataMapper.insertAll();
	}

	public void closeDataStreams()
	{
		itemDataMapper.closeConnection();
	}

	public OLDDataMapper getItemDataMapper()
	{
		return itemDataMapper;
	}

	public void setFollowing(boolean following)
	{
		this.following = following;
	}

	public void setPartner(boolean partner)
	{
		this.partner = partner;
	}

	/**This methods do nothing, because this base class is not supposed to change the values of properties
	 * @param index
	 * @param value
	 */
	public void setProductName(int index, String value) { }

	public void setProductDescription(int index, String value) { }

	public void setProductID(int index, String value) { }

	public void setProductBarcode(int index, String value) { }

	public void setProductPackSizeNumeric(int index, BigDecimal value) { }

	public void setProductCommodityCode(int index, String value) { }

	public void setProductKeywords(int index, List<KeywordType> value) { }

	public void setProductKeywords(int index, String value) {}

	/**Returns String representing BussinesParty class. Used as the node name in the tree model.
	 * @return the name of the core Party or null if core Party is not set
	 */
	@Override
	public String toString()
	{
		return InstanceFactory.getPropertyOrNull(coreParty.getPartyName().get(0).getName(), NameType::getValue);
	}

}