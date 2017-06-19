package rs.ruta.client;

import java.math.BigDecimal;
import java.util.*;

import javax.xml.bind.annotation.*;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import rs.ruta.client.datamapper.*;

@XmlRootElement(name = "BussinesParty", namespace = "urn:rs:ruta:client:businessparty")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessParty
{

	@XmlElement(name = "MyProducts")
//	@XmlTransient
	private ArrayList<ItemType> myProducts; //database alternative - MMM: to be replaced with real database

	@XmlTransient
	private ItemTypeFileMapper<ItemType> itemDataMapper;

	@XmlElement(name = "CoreParty")
	private PartyType coreParty;

	@XmlElement(name = "Following")
	private boolean following;

	@XmlElement(name = "Partner")
	private boolean partner;

	public BusinessParty()
	{
		myProducts = getMyProducts();
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

	public PartyType getCoreParty()
	{
		if(coreParty == null)
			coreParty = new PartyType();
		return coreParty;
	}

	public void setCoreParty(PartyType coreParty)
	{
		this.coreParty = coreParty;
	}

	public ArrayList<ItemType> getMyProducts()
	{
		if (myProducts == null)
			myProducts = new ArrayList<ItemType>();
		return myProducts;
	}

	public void setMyProducts(ArrayList<ItemType> myProducts)
	{
		this.myProducts = myProducts;
	}

	public void setItemDataMapper(ItemTypeFileMapper<ItemType> itemDataMapper)
	{
		this.itemDataMapper = itemDataMapper;
	}

	public void setItemDataMapper(String fileStore)
	{
		this.itemDataMapper = new ItemTypeFileMapper<ItemType>(this, fileStore);
	}


	public String getProductName(int index)
	{
		ItemType item = myProducts.get(index);
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

	public String getProductDescription(int index)
	{
		ItemType item = myProducts.get(index);
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

	public String getProductID(int index)
	{
		ItemType item = myProducts.get(index);
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

	public String getProductBarcode(int index)
	{
		ItemType item = myProducts.get(index);
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

	public BigDecimal getProductPackSizeNumeric(int index) //MMM: check if return value should be String instead
	{
		ItemType item = myProducts.get(index);
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

	public String getProductCommodityCode(int index)
	{
		ItemType item = myProducts.get(index);
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

	public String getProductItemClassificationCode(int index)
	{
		ItemType item = myProducts.get(index);
		/*List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() != 0)
		{
			CommodityClassificationType commodityClass = commodities.get(0);
			if(commodityClass != null)
			{
				ItemClassificationCodeType classificationCode = commodityClass.getItemClassificationCode();
				if(classificationCode != null)
					return classificationCode.toString();
			}
		}
		return null;*/

		try
		{
			return InstanceFactory.getPropertyOrNull(item.getCommodityClassification().get(0).getItemClassificationCode(), ItemClassificationCodeType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void addNewEmptyProduct()
	{
//		myProducts.add(InstanceFactory.newInstanceItemType());
		myProducts.add(new ItemType());
	}

	public int getProductCount()
	{
		return myProducts.size();
	}

	/**
	 * Reads all products of party from the store. If the products are already read, skips the read.
	 */
	public void importMyProducts()
	{
		if(myProducts.size() == 0)
			myProducts = (ArrayList<ItemType>) itemDataMapper.findAll();
		if(myProducts == null) // nothing found
		{
			myProducts = new ArrayList<ItemType>();
		}
	}

	/**
	 * Writes all Products to the database.
	 */
	public void exportMyProducts()
	{
		itemDataMapper.insertAll();
	}

	public void closeDataStreams()
	{
		itemDataMapper.closeConnection();
	}

	public DataMapper getItemDataMapper()
	{
		return itemDataMapper;
	}

}