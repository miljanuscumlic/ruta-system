package rs.ruta.client;

import java.math.BigDecimal;
import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.CatalogueType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import rs.ruta.client.datamapper.*;

//@XmlRootElement(name = "BussinesParty", namespace = "urn:rs:ruta:client:businessparty") //commented because it is not root xml element anymore, MyParty is instead
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessParty
{
//	@XmlElement(name = "MyProducts")
//	@XmlTransient
	@XmlElement(name = "MyProduct")
	private ArrayList<ItemType> myProducts; //database alternative - MMM: to be replaced with real database
	@XmlTransient
	private ItemTypeBinaryFileMapper<ItemType> itemDataMapper;
	@XmlElement(name = "CoreParty")
//	@XmlTransient
	private Party coreParty;
	@XmlElement(name = "Following")
//	@XmlTransient
	private boolean following;
	@XmlElement(name = "Partner")
//	@XmlTransient
	private boolean partner;
	@XmlElement(name = "CatalogueID")
//	@XmlTransient
	protected long catalogueID;
	@XmlElement(name = "CatalogueDeletionID")
	protected long catalogueDeletionID;

	protected XMLGregorianCalendar catalogueIssueDate;


	public BusinessParty()
	{
		myProducts = getMyProducts();
		catalogueID = 0;
		catalogueDeletionID = 0;
		catalogueIssueDate = null;
		coreParty = null;
	}

	/**Returns ID of the latest created Catalogue Document.
	 * @return catalogue ID
	 */
	public long getCatalogueID()
	{
		return catalogueID;
	}

	public void setCatalogueID(Long catalogueID)
	{
		this.catalogueID = catalogueID;
	}

	/**Returns next ID for the newly created Catalogue Document.
	 * @return next catalogue ID
	 */
	public long nextCatalogueID()
	{
		return ++catalogueID;
	}

	/**Returns ID of the latest created Catalogue Deletion Document.
	 * @return catalogue deletion ID
	 */
	public long getCatalogueDeletionID()
	{
		return catalogueDeletionID;
	}

	public void setCatalogueDeletionID(Long catalogueDeletionID)
	{
		this.catalogueDeletionID = catalogueDeletionID;
	}

	/**Gets issue date of the latest catalogue
	 * @return catalogue issue date
	 */
	public XMLGregorianCalendar getCatalogueIssueDate()
	{
		return catalogueIssueDate;
	}

	/**Sets catalogue issue date to current date
	 * @return set catalogue issue date
	 */
	public XMLGregorianCalendar setCatalogueIssueDate()
	{
		this.catalogueIssueDate = InstanceFactory.getDate();
		return catalogueIssueDate;
	}

	/**Returns next ID for the newly created CatalogueDeletion Document.
	 * @return next catalogue deletion ID
	 */
	public long nextCatalogueDeletionID()
	{
		return ++catalogueDeletionID;
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
		if (myProducts == null)
			myProducts = new ArrayList<ItemType>();
		return myProducts;
	}

	public void setMyProducts(ArrayList<ItemType> myProducts)
	{
		this.myProducts = myProducts;
	}

	public void setMyProducts(CatalogueType catalogue)
	{
		try
		{
			String strID = InstanceFactory.getPropertyOrNull(catalogue.getID(), IDType::getValue);
			catalogueID = Integer.parseInt(strID);
		}
		catch(Exception e)
		{
			catalogueID = 0;
		}
		List<CatalogueLineType> catalogueLines = catalogue.getCatalogueLine();
		if(catalogueLines.size() != 0)
		{
			myProducts.clear();
			for(CatalogueLineType line : catalogueLines)
				myProducts.add(line.getItem());
				//myProducts.add(InstanceFactory.newInstance(line.getItem()));
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

	/**Returns the number of party's products.
	 * @return number of products
	 */
	public int getProductCount()
	{
		return myProducts.size();
	}

	/**
	 * Reads all products of party from the data store. If the products are already read, skips the read.
	 */
	public void importMyProducts()
	{
		if(itemDataMapper != null)
		{
			if(myProducts.size() == 0)
			{
				myProducts = (ArrayList<ItemType>) itemDataMapper.findAll();
				if(myProducts == null) // nothing found
					myProducts = new ArrayList<ItemType>();
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

	public void setCatalogueID(long catalogueID)
	{
		this.catalogueID = catalogueID;
	}

	/**This methods do nothing, because this base class is not suppose to change the values of properties
	 * @param index
	 * @param value
	 */
	public void setProductName(int index, String value) { }

	public void setProductDescription(int index, String value) { }

	public void setProductID(int index, String value) { }

	public void setProductBarcode(int index, String value) { }

	public void setProductPackSizeNumeric(int index, BigDecimal value) { }

	public void setProductCommodityCode(int index, String value) { }

	public void setProductItemClassificationCode(int index, String value) { }

	/**Returns String representing BussinesParty class.
	 * @return the name of the core Party or null if core Party is not set
	 */
	@Override
	public String toString()
	{
		return InstanceFactory.getPropertyOrNull(coreParty.getPartyName().get(0).getName(), NameType::getValue);
	}

}