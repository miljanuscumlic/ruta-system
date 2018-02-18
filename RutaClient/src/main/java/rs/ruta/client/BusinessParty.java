package rs.ruta.client;

import java.math.BigDecimal;
import rs.ruta.common.InstanceFactory;
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
	@XmlElement(name = "Product")
	private ArrayList<ItemType> products; //MMM: this should be moved to MyParty class or delete altogether and catalogue field used instead
	@XmlElement(name ="Catalogue")
	private CatalogueType catalogue; //MMM: maybe CatalogueType should be changed with Catalogue like a Party is instead of PartyType
	@XmlTransient
	private ItemTypeBinaryFileMapper<ItemType> itemDataMapper;
	@XmlElement(name = "CoreParty")
//	@XmlTransient
	private Party coreParty;
	/**
	 * True if the party is followed by MyParty
	 */
	@XmlElement(name = "Following")
	private boolean following; // MMM: is it necessary - at this point of development no - maybe in the future.
	/**
	 * True if the party is a business partner of MyParty.
	 */
	@XmlElement(name = "Partner")
	private boolean partner; //MMM: is it necessary? - yes if I stay with the compound list of all following parties
	/**
	 * True if the party is archived one.
	 */
	@XmlElement(name = "Archived")
	private boolean archived;
	@XmlElement(name = "Deregistered")
	private boolean deregistered;

	public BusinessParty()
	{
		products = getProducts();
		coreParty = null;
	}

	@Override
	protected BusinessParty clone()
	{
		BusinessParty bp = new BusinessParty();
		bp.itemDataMapper = itemDataMapper;
		bp.following = following;
		bp.partner = partner;
		bp.archived = archived;
		bp.deregistered = deregistered;
		if(coreParty != null)
			bp.coreParty = coreParty.clone();
		if(catalogue != null)
			bp.catalogue = catalogue.clone();
		bp.products = new ArrayList<>(); // overriding old list by creating a new one of cloned products
		for(ItemType item : products)
			bp.products.add(item != null? item.clone() : null);
		return bp;
	}

	/**Checks whether the party is followed by MyParty.
	 * @return true if the party is followed by MyParty
	 */
	public boolean isFollowing()
	{
		return following;
	}
/*	//MMM:commented to see whether JAXB is going to complain about it
	public void setFollowing(Boolean following) // Boolean not boolean because of JAXB
	{
		this.following = following;
	}

	public void setPartner(Boolean partner)
	{
		this.partner = partner;
	}*/

	public void setFollowing(boolean following)
	{
		this.following = following;
	}

	/**Checks whether the party is a business partner of MyParty.
	 * @return true if the party is a business partner of MyParty
	 */
	public boolean isPartner()
	{
		return partner;
	}

	public void setPartner(boolean partner)
	{
		this.partner = partner;
	}

	/**Checks whether the party is archived one.
	 * @return true if the party is archived one
	 */
	public boolean isArchived()
	{
		return archived;
	}

	public void setArchived(boolean archived)
	{
		this.archived = archived;
	}

	/**Checks whether the party is deregistered.
	 * @return true if the party is deregistered
	 */
	public boolean isDeregistered()
	{
		return deregistered;
	}

	public void setDeregistered(boolean deregistered)
	{
		this.deregistered = deregistered;
	}

	public CatalogueType getCatalogue()
	{
		return catalogue;
	}

	public void setCatalogue(CatalogueType catalogue)
	{
		this.catalogue = catalogue;
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

	public void setCoreParty(PartyType coreParty)
	{
		this.coreParty = new Party(coreParty);
	}

	public boolean hasCoreParty()
	{
		return coreParty == null ? false : true;
	}

	/**Gets Party's ID or {@code null} if ID is not set.
	 * @return ID or {@code null} if ID has not been set
	 */
	public String getPartyID()
	{
		return getCoreParty().getPartyID();
	}

	/**Gets the simple name of the party.
	 * @return party's name
	 */
	public String getPartyName()
	{
		return getCoreParty().getSimpleName();
	}

	public ArrayList<ItemType> getProducts()
	{
		if (products == null)
			products = new ArrayList<ItemType>();
		return products;
	}

	public void clearProducts()
	{
		this.products = null;
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
		if(catalogueLines.size() != 0) //MMM: could be done with the streams
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

	/**Adds new empty {@link ItemType} to the product list.
	 */
	public void addNewEmptyProduct()
	{
		products.add(new ItemType());
	}

	/**Removes {@link ItemType} from the product list.
	 * @param row product's number
	 */
	public void removeProduct(int row)
	{
		products.remove(row);
	}

	/**Adds passed {@link ItemType} to the product list.
	 * @param item {@link ItemType} to be added
	 */
	public void addProduct(ItemType item)
	{
		products.add(item);
	}

	/**Returns the number of party's products.
	 * @return number of products
	 */
	public int getProductCount()
	{
		return products.size();
	}

	/**Reads all products of the {@link Party} from the data store. If products have been already read, skips the read.
	 */
	public void importProducts()
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
	public void exportProducts()
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

	/**Checks whether two parties are the same. Check is based on equality of party IDs.
	 * @param partyOne
	 * @param partyTwo
	 * @return true if parties are the same, false if they are not the same or some party has not set
	 * its party ID.
	 */
	public static boolean sameParties(BusinessParty partyOne, BusinessParty partyTwo)
	{
		return Party.sameParties(partyOne.getCoreParty(), partyTwo.getCoreParty());
	}

	/**Checks whether two parties are the same. Check is based on equality of party IDs.
	 * @param partyOne
	 * @param partyTwo
	 * @return true if parties are the same, false if they are not the same or some party has not set
	 * its party ID.
	 */
	public static boolean sameParties(BusinessParty partyOne, PartyType partyTwo)
	{
		return Party.sameParties(partyOne.getCoreParty(), partyTwo);
	}

}