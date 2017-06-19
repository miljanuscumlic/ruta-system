package rs.ruta.client;

import java.math.BigDecimal;
import java.util.*;

import javax.xml.bind.annotation.*;

import oasis.names.specification.ubl.schema.xsd.catalogue_2.*;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.*;
import rs.ruta.client.datamapper.*;

@XmlRootElement(name = "BusinessParty", namespace = "urn:rs:ruta:client")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessParty implements BusinessPartyInterface
{
//	@XmlElement(name = "BusinessPartners")
	@XmlTransient
	private List<PartyType> businessPartners;
//	@XmlElement(name = "FollowingPartners")
	@XmlTransient
	private List<PartyType> followingParties;
//	@XmlElement(name = "FollowerPartners")
	@XmlTransient
	private List<PartyType> followerParties;
//	@XmlElement(name = "MyProducts")
	@XmlTransient
	private ArrayList<ItemType> myProducts; //database alternative - MMM: to be replaced with real database

	@XmlTransient
	private ItemTypeFileMapper<ItemType> itemDataMapper;

	@XmlElement(name = "CatalogueID")
	private long catalogueID;
	@XmlElement(name = "DirtyCatalogue")
	private boolean dirtyCatalogue; // MMM: this property is also saved as the preference
	@XmlElement(name = "CoreParty")
	private PartyType coreParty;

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

	public BusinessParty()
	{
		myProducts = getMyProducts();
		catalogueID = 0;
	}

	public List<PartyType> getBusinessPartners()
	{
		if (businessPartners == null)
			businessPartners = new ArrayList<PartyType>();
		return businessPartners;
	}

	public void setBusinessPartners(List<PartyType> businessPartners)
	{
		this.businessPartners = businessPartners;
	}

	public List<PartyType> getFollowingParties()
	{
		if (followingParties == null)
			followingParties = new ArrayList<PartyType>();
		return followingParties;
	}

	public void setFollowingParties(List<PartyType> followingParties)
	{
		this.followingParties = followingParties;
	}

	public List<PartyType> getFollowerParties()
	{
		if (followerParties == null)
			followerParties = new ArrayList<PartyType>();
		return followerParties;
	}

	public void setFollowerParties(List<PartyType> followerParties)
	{
		this.followerParties = followerParties;
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

	public boolean isDirtyCatalogue()
	{
		return dirtyCatalogue;
	}

	public void setDirtyCatalogue(Boolean dirty)
	{
		dirtyCatalogue = dirty;
	}

	public void setItemDataMapper(ItemTypeFileMapper<ItemType> itemDataMapper)
	{
		this.itemDataMapper = itemDataMapper;
	}

	public void setItemDataMapper(String fileStore)
	{
		this.itemDataMapper = new ItemTypeFileMapper<ItemType>(this, fileStore);
	}

	public long getCatalogueID() { return catalogueID; }

	public void setCatalogueID(Long catalogueID)
	{
		this.catalogueID = catalogueID;
	}

	public void setDirtyCatalogue(boolean dirtyCatalogue)
	{
		this.dirtyCatalogue = dirtyCatalogue;
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

	public void setProductName(int index, String value)
	{
		ItemType item = myProducts.get(index);
		if(item.getName() == null)
			item.setName(new NameType());
		if(hasCellValueChanged(item.getName().getValue(), value))
			item.getName().setValue(value);
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

	public void setProductDescription(int index, String value)
	{
		ItemType item = myProducts.get(index);
		List<DescriptionType> descriptions = item.getDescription();
		if(descriptions.size() == 0)
			descriptions.add(new DescriptionType());
		if(hasCellValueChanged(descriptions.get(0).getValue(), value))
			descriptions.get(0).setValue(value);
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

	public void setProductID(int index, String value)
	{
		ItemType item = myProducts.get(index);
		if(item.getSellersItemIdentification() == null)
			item.setSellersItemIdentification(new ItemIdentificationType());
		if(item.getSellersItemIdentification().getID() == null)
			item.getSellersItemIdentification().setID(new IDType());
		if(hasCellValueChanged(item.getSellersItemIdentification().getID().getValue(), value))
			item.getSellersItemIdentification().getID().setValue(value);
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

	public void setProductBarcode(int index, String value)
	{
		ItemType item = myProducts.get(index);
		if(item.getSellersItemIdentification() == null)
			item.setSellersItemIdentification(new ItemIdentificationType());
		if(item.getSellersItemIdentification().getBarcodeSymbologyID() == null)
			item.getSellersItemIdentification().setBarcodeSymbologyID(new BarcodeSymbologyIDType());
		if(hasCellValueChanged(item.getSellersItemIdentification().getBarcodeSymbologyID().getValue(), value))
			item.getSellersItemIdentification().getBarcodeSymbologyID().setValue(value);

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

	public void setProductPackSizeNumeric(int index, String value)
	{
		ItemType item = myProducts.get(index);
		if(item.getPackSizeNumeric() == null)
			item.setPackSizeNumeric(new PackSizeNumericType());
		if(hasCellValueChanged(item.getPackSizeNumeric().getValue().toString(), value))
			item.getPackSizeNumeric().setValue(BigDecimal.valueOf(Long.parseLong(value)));
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


	public void setProductCommodityCode(int index, String value)
	{
		ItemType item = myProducts.get(index);
		List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() == 0)
			commodities.add(new CommodityClassificationType());
		if(commodities.get(0).getCommodityCode() == null)
			commodities.get(0).setCommodityCode(new CommodityCodeType());
		if(hasCellValueChanged(commodities.get(0).getCommodityCode().getValue(), value))
			commodities.get(0).getCommodityCode().setValue(value);;
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


	public void setProductItemClassificationCode(int index, String value)
	{
		ItemType item = myProducts.get(index);
		List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.size() == 0)
			commodities.add(new CommodityClassificationType());
		if(commodities.get(0).getItemClassificationCode() == null)
			commodities.get(0).setItemClassificationCode(new ItemClassificationCodeType());
		if(hasCellValueChanged(commodities.get(0).getItemClassificationCode().getValue(), value))
			commodities.get(0).getItemClassificationCode().setValue(value);;
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
			catalogueID = 0L;
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

	/**Generates Catalogue Document from Items in the Product table.
	 * @return catalogue
	 */
	public CatalogueType createCatalogueTypeObject()
	{
		//forming Catalogue document
		CatalogueType catalogue = new CatalogueType();
		IDType catID = new IDType();
		catID.setValue(String.valueOf(catalogueID++));
		catalogue.setID(catID);
		IssueDateType date = new IssueDateType();
		date.setValue(InstanceFactory.getDate());
		catalogue.setIssueDate(date);

		// MMM: insert here Provider Party and Receiver Party

		int cnt = 0;
		for(ItemType prod : myProducts)
		{
			CatalogueLineType catLine = new CatalogueLineType();
			IDType catLineID = new IDType();
			catLineID.setValue(catID.getValue() + "-" + cnt++);
			catLine.setID(catLineID);
			catLine.setItem(prod);
			catalogue.getCatalogueLine().add(catLine);
		}
		return catalogue;
	}

	/**Checks if the cell value has changed. If it has changed dirtyCatalogue field is set to true
	 * @param oldOne old value of the cell
	 * @param newOne new value of the cell
	 * @return true if the values differs, false otherwise
	 */
	private <T> boolean hasCellValueChanged(T oldOne, T newOne)
	{
		boolean changed = ! newOne.equals(oldOne);
		dirtyCatalogue = dirtyCatalogue || changed;
		return changed;
	}


}