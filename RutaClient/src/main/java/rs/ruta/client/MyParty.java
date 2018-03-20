package rs.ruta.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import oasis.names.specification.ubl.schema.xsd.catalogue_21.*;
import oasis.names.specification.ubl.schema.xsd.cataloguedeletion_21.CatalogueDeletionType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import rs.ruta.client.gui.RutaTreeModel;
import rs.ruta.common.BusinessPartySearchCriterion;
import rs.ruta.common.CatalogueSearchCriterion;
import rs.ruta.common.DeregistrationNotice;
import rs.ruta.common.InstanceFactory;
import rs.ruta.common.PartySearchCriterion;
import rs.ruta.common.RutaUser;
import rs.ruta.common.datamapper.DataMapper;
import rs.ruta.common.datamapper.DetailException;
import rs.ruta.common.datamapper.MapperRegistry;

@XmlRootElement(name = "MyParty", namespace = "urn:rs:ruta:client")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({CatalogueType.class}) //this solves the issue JAXB context not seeing the CatatalogueType
public class MyParty extends BusinessParty
{
	private static Logger logger = LoggerFactory.getLogger("rs.ruta.client");
	@XmlElement(name = "LocalUser")
	private RutaUser localUser;
	@XmlElement(name = "CDRUser")
	private RutaUser cdrUser;
	@XmlTransient
	private List<Item> products;
	/**
	 * MyParty data retrieved from the CDR service.
	 */
//	@XmlElement(name = "MyFollowingParty")
	@XmlTransient
	@Nullable
	private BusinessParty myFollowingParty;
	/**
	 * List of all following parties of MyParty. Should always be accessed with {@link #getFollowingParty(String)}
	 * method call unless prior to access is checked for {@code null}.
	 */
//	@XmlElement(name = "FollowingParty")
	@XmlTransient
	@Nullable
	private List<BusinessParty> followingParties;
	/**
	 * Helper list containing only business partners of MyParty.
	 * Should always be accessed with {@link #getBusinessPartners()} method call unless prior to access is checked for {@code null}.
	 */
	@XmlTransient
	@Nullable
	private List<BusinessParty> businessPartners;
	/**
	 * Helper list containing only following parties that are not business partners of MyParty.
	 * Should always be accessed with {@link #getOtherParties()} method call unless prior to access is checked for {@code null}.
	 */
	@XmlTransient
	@Nullable
	private List<BusinessParty> otherParties;
	/**
	 * List of unfollowed parties from the CDR service.
	 * Should always be accessed with {@link #getArchivedParties()} method call unless prior to access is checked for {@code null}.
	 */
//	@XmlElement(name = "ArchivedParty")
	@XmlTransient
	@Nullable
	private List<BusinessParty> archivedParties;
	/**
	 * List of deregistered parties from the CDR service.
	 * Should always be accessed with {@link #getArchivedParties()} method call unless prior to access is checked for {@code null}.
	 */
//	@XmlElement(name = "DeregisteredParty")
	@XmlTransient
	@Nullable
	private List<BusinessParty> deregisteredParties;
//	@XmlElement(name = "PartySearch")
	@XmlTransient
	private List<Search<PartyType>> partySearches;
//	@XmlElement(name = "CatalogueSearch")
	@XmlTransient
	private List<Search<CatalogueType>> catalogueSearches;
	@XmlTransient
	private List<ActionListener> actionListeners;
	@XmlElement(name = "DirtyCatalogue")
	private boolean dirtyCatalogue;
	@XmlElement(name = "DirtyMyParty")
	private boolean dirtyMyParty;
	@XmlElement(name = "InsertMyCatalogue")
	private boolean insertMyCatalogue; // true when catalogue should be inserted in the CDR i.e. deposited for the first time
	@XmlElement(name = "SearchNumber")
	private long searchNumber;
	@XmlElement(name = "CatalogueID")
	private long catalogueID;
	@XmlElement(name = "CatalogueDeletionID")
	private long catalogueDeletionID;
	@XmlElement(name = "ItemID")
	private long itemID;
	@XmlElement(name = "CatalogueIssueDate")
	protected XMLGregorianCalendar catalogueIssueDate;
	@XmlAttribute(name = "JAXBVersion")
	private String jaxb; // version of the MyParty class

	public MyParty()
	{
		super();
		setFollowing(true);
		products = getProducts();
		dirtyCatalogue = dirtyMyParty = insertMyCatalogue = true;
//		username = password = secretKey = null;
		localUser = new RutaUser();
		cdrUser = new RutaUser();
		followingParties = businessPartners = otherParties = archivedParties = deregisteredParties = null;
		searchNumber = catalogueID = catalogueDeletionID = itemID = 0;
		catalogueIssueDate = null;
		actionListeners = new ArrayList<>();;
		jaxb = RutaClient.getVersion().getJaxbVersion();
	}

	/**
	 * Populates all lists of {@code MyParty} from a local data store.
	 * @throws DetailException if data could not be retrieved from a data store
	 */
	public void loadData() throws DetailException
	{
		final MapperRegistry mapperRegistry = MapperRegistry.getInstance();
		setProducts(mapperRegistry.getMapper(Item.class).findAll());
		BusinessPartySearchCriterion criterion = new BusinessPartySearchCriterion();
		criterion.setPartner(true);
		final DataMapper<BusinessParty, String> businessPartyMapper = mapperRegistry.getMapper(BusinessParty.class);
		businessPartners = businessPartyMapper.findMany(criterion);
		criterion = new BusinessPartySearchCriterion();
		criterion.setOther(true);
		otherParties = businessPartyMapper.findMany(criterion);
		final String myPartyID = getPartyID();
		if(myPartyID != null)
			if(otherParties != null)
			{
				try
				{
					myFollowingParty = getOtherParties().stream().filter(party -> party.getPartyID().equals(myPartyID)).findFirst().get();
					otherParties.remove(myFollowingParty);
				}
				catch(NoSuchElementException e)
				{ //OK; myFollowingParty is not among the following parties
					myFollowingParty = businessPartyMapper.find(myPartyID);
				}
			}
			else
				myFollowingParty = businessPartyMapper.find(myPartyID);



/*		criterion = new BusinessPartySearchCriterion();
		criterion.setFollowing(true);
		final List<BusinessParty> followings = mapperRegistry.getMapper(BusinessParty.class).findMany(criterion);
		if(getPartyID() != null)
		{
			setMyFollowingParty(mapperRegistry.getMapper(BusinessParty.class).find(getPartyID()));
			//extract myFollowingParty from all followings parties
			try
			{
				final BusinessParty myFP = followings.stream().filter(party -> party.getPartyID().equals(getPartyID())).findFirst().get();
				followings.remove(myFP);
			}
			catch(NoSuchElementException e) { } //OK; myFollowingParty is not among the following parties
		}
		setFollowingParties(followings);*/


		criterion = new BusinessPartySearchCriterion();
		criterion.setArchived(true);
		setArchivedParties(businessPartyMapper.findMany(criterion));
		criterion = new BusinessPartySearchCriterion();
		criterion.setDeregistered(true);
		setDeregisteredParties(businessPartyMapper.findMany(criterion));
		setPartySearches(Search.listToListOfGenerics(mapperRegistry.getMapper(PartySearch.class).findAll()));
		setCatalogueSearches(Search.listToListOfGenerics(mapperRegistry.getMapper(CatalogueSearch.class).findAll()));
	}

	/**
	 * Stores My Party data to a local data store.
	 * @throws Exception if data could not be stored to a data store
	 */
	public void storeData() throws Exception
	{
		setSearchNumber(Search.getSearchNum());
		final MapperRegistry mapperRegistry = MapperRegistry.getInstance();
		mapperRegistry.getMapper(MyParty.class).insert(getLocalUsername(), this);
		mapperRegistry.getMapper(Item.class).insertAll(null, getProducts());
/*		if(followingParties != null && !followingParties.isEmpty())
			mapperRegistry.getMapper(BusinessParty.class).insertAll(null, getFollowingParties());*/
		if(businessPartners != null && !businessPartners.isEmpty())
			mapperRegistry.getMapper(BusinessParty.class).insertAll(null, businessPartners);
		if(otherParties != null && !otherParties.isEmpty())
			mapperRegistry.getMapper(BusinessParty.class).insertAll(null, otherParties);
		if(archivedParties != null && !archivedParties.isEmpty())
			mapperRegistry.getMapper(BusinessParty.class).insertAll(null, archivedParties);
		if(deregisteredParties != null && !deregisteredParties.isEmpty())
			mapperRegistry.getMapper(BusinessParty.class).insertAll(null, deregisteredParties);
		if(myFollowingParty != null)
			mapperRegistry.getMapper(BusinessParty.class).insert(null, myFollowingParty);
		if(partySearches != null && !partySearches.isEmpty())
		{
			List<PartySearch> newList = Search.listFromLisfOfGenerics(getPartySearches());
			mapperRegistry.getMapper(PartySearch.class).insertAll(null, newList);
		}
		if(catalogueSearches != null && !catalogueSearches.isEmpty())
		{
			List<CatalogueSearch> newList = Search.listFromLisfOfGenerics(getCatalogueSearches());
			mapperRegistry.getMapper(CatalogueSearch.class).insertAll(null, newList);
		}
	}

	/**
	 * Deletes all the objects from the data model and all the data from the data store.
	 * @throws DetailException if data could not be deleted
	 */
	public void deleteData() throws DetailException
	{
		dirtyCatalogue = dirtyMyParty = insertMyCatalogue = true;
		searchNumber = catalogueID = catalogueDeletionID = itemID = 0;
		catalogueIssueDate = null;
		Search.resetSearchNumber();
		clearParties();
		clearSearches();
		clearProducts();
	}

	public List<ActionListener> getActionListeners()
	{
		return actionListeners;
	}

	/**
	 * Registeres new {@link ActionListener} with {@code MyParty} object.
	 * @param listener
	 */
	public void addActionListener(ActionListener listener)
	{
		actionListeners.add(listener);
	}

	/**
	 * Notifies all registered {@link ActionListener}s sending {@link ActionEvent} that describes the change
	 * in the data model.
	 * @param event
	 */
	public void notifyListeners(ActionEvent event)
	{
		actionListeners.stream().
		filter(listener -> ((RutaTreeModel) listener).listenFor(event.getClass())).
		forEach(listener -> listener.actionPerformed(event));

/*		List<ActionListener> listeners = actionListeners.stream().filter(listener -> ((RutaTreeModel) listener).
				listenFor(event.getClass())).collect(Collectors.toList());
		for(ActionListener listener : listeners)
			listener.actionPerformed(event);*/
	}

	public List<Item> getProducts()
	{
		if (products == null)
			products = new ArrayList<Item>();
		return products;
	}

	public void setProducts(@Nullable List<Item> products)
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
		if(!catalogueLines.isEmpty())
		{
			products.clear();
			products = catalogueLines.stream().map(line -> new Item(line.getItem())).collect(Collectors.toList());
/*			for(CatalogueLineType line : catalogueLines)
				products.add(line.getItem());*/
		}
	}

	/**
	 * Removes all products from the data model and data store.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @throws DetailException if data could not be deleted from the database
	 */
	public void clearProducts() throws DetailException
	{
		MapperRegistry.getInstance().getMapper(Item.class).deleteAll();
		//MMM:	TODO	notifyListeners(new BusinessPartyEvent(products, ItemEvent.ALL_PRODUCTS_REMOVED));
		this.products = null;
	}

	public String getProductNameAsString(final int index)
	{
		final Item item = products.get(index);
		try
		{
			return InstanceFactory.getPropertyOrNull(item.getName(), NameType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductName(int index, String value)
	{
		value = value.trim();
		final Item item = products.get(index);
		if(hasCellValueChanged(InstanceFactory.getPropertyOrNull(item.getName(), NameType::getValue), value))
			item.setName(value);
	}

	public String getProductDescriptionAsString(final int index)
	{
		final Item item = products.get(index);
		try
		{
			return InstanceFactory.getPropertyOrNull(item.getDescription().get(0), DescriptionType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductDescription(int index, String value)
	{
		value = value.trim();
		final Item item = products.get(index);
		List<DescriptionType> descriptions = item.getDescription();
		if(descriptions.isEmpty())
			descriptions.add(new DescriptionType());
		if(hasCellValueChanged(descriptions.get(0).getValue(), value))
			descriptions.get(0).setValue(value);
	}

	public String getProductIDAsString(final int index)
	{
		final Item item = products.get(index);
		try
		{
			return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getID(), IDType::getValue);
		}
		catch(Exception e) { return null; }
	}

	/**
	 * Sets product's ID which is unique in the scope of MyParty.
	 * @param index product's index in the list of all products
	 * @param value ID's value to set
	 * @throws ProductException if product's ID is not unique or is equal to an empty string
	 */
	public void setProductID(int index, String value) throws ProductException
	{
		value = value.trim();
		if("".equals(value))
			throw new ProductException("Product ID must be a non-empty string value.\nChange you made have not been accepted.");
		if(!isUniqueProductID(value))
			throw new ProductException("Product ID is not unique.");
		final Item item = products.get(index);
		if(item.getSellersItemIdentification() == null)
			item.setSellersItemIdentification(new ItemIdentificationType());
		if(item.getSellersItemIdentification().getID() == null)
			item.getSellersItemIdentification().setID(new IDType());
		if(hasCellValueChanged(item.getSellersItemIdentification().getIDValue(), value))
			item.getSellersItemIdentification().setID(value);
	}


	public String getProductBarcodeAsString(final int index)
	{
		final Item item = products.get(index);
		try
		{
			return InstanceFactory.getPropertyOrNull(item.getSellersItemIdentification().getBarcodeSymbologyID(), BarcodeSymbologyIDType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductBarcode(int index, String value) throws ProductException
	{
		value = value.trim();
		final Item item = products.get(index);
		if(item.getSellersItemIdentification() == null)
			//item.setSellersItemIdentification(new ItemIdentificationType());
			throw new ProductException("Product ID is mandatory, and it must be entered first!");
		if(item.getSellersItemIdentification().getBarcodeSymbologyID() == null)
			item.getSellersItemIdentification().setBarcodeSymbologyID(new BarcodeSymbologyIDType());
		if(hasCellValueChanged(item.getSellersItemIdentification().getBarcodeSymbologyIDValue(), value))
			item.getSellersItemIdentification().setBarcodeSymbologyID(value);
	}

	public BigDecimal getProductPackSize(final int index) //MMM: check whether return value should be String instead
	{
		final Item item = products.get(index);
		try
		{
			return InstanceFactory.getPropertyOrNull(item.getPackSizeNumeric(), PackSizeNumericType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductPackSizeNumeric(int index, BigDecimal value)
	{
		final Item item = products.get(index);
		if(item.getPackSizeNumeric() == null)
			item.setPackSizeNumeric(new PackSizeNumericType());
		if(hasCellValueChanged(item.getPackSizeNumericValue(), value))
			if(value.doubleValue() == 0) // delete PackSizeNumericType field because of UBL conformance
				item.setPackSizeNumeric((PackSizeNumericType) null);
			else
				item.setPackSizeNumeric(value);
	}

	public String getProductCommodityCodeAsString(final int index)
	{
		final Item item = products.get(index);
		try
		{
			return InstanceFactory.getPropertyOrNull(item.getCommodityClassification().get(0).getCommodityCode(),
					CommodityCodeType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductCommodityCode(int index, String value)
	{
		value = value.trim();
		final Item item = products.get(index);
		List<CommodityClassificationType> commodities = item.getCommodityClassification();
		if(commodities.isEmpty())
			commodities.add(new CommodityClassificationType());
		if(commodities.get(0).getCommodityCode() == null)
			commodities.get(0).setCommodityCode(new CommodityCodeType());
		if(hasCellValueChanged(commodities.get(0).getCommodityCodeValue(), value))
			commodities.get(0).setCommodityCode(value);;
	}

	public BigDecimal getProductPrice(final int index)
	{
		try
		{
			final Item item = products.get(index);
			return InstanceFactory.getPropertyOrNull(item.getPrice().getPriceAmount(), PriceAmountType::getValue);
		}
		catch(Exception e) { return null; }
	}

	public void setProductPrice(int index, final BigDecimal value)
	{
		final Item item = products.get(index);
		if(item.getPrice() == null)
			item.setPrice(new PriceType());
		if(item.getPrice().getPriceAmount() == null)
		{
			// to conform to the UBL, currencyID is mandatory
			PriceAmountType priceAmount = new PriceAmountType(value);
			priceAmount.setCurrencyID("RSD"); // MMM: currencyID should be pooled from somewhere in the UBL definitions - check specifications
			item.getPrice().setPriceAmount(priceAmount);
		}
		if(hasCellValueChanged(item.getPrice().getPriceAmountValue(), value))
			item.getPrice().setPriceAmount(value);
	}

	public String getProductTaxPrecentAsString(final int index)
	{
		try
		{
			final Item item = products.get(index);
			return InstanceFactory.getPropertyOrNull(item.getClassifiedTaxCategoryAtIndex(0), TaxCategoryType::getPercentValue).toString();
		}
		catch(Exception e) { return null; }
	}

	/**
	 * Sets the tax precentage. It is equivalent and it is done by changing of the tax category.
	 * @param index product's index in the list
	 * @param value value to set
	 */
	public void setProductTaxPrecent(final int index, String value)
	{
		value = value.trim();
		final List<TaxCategoryType> taxCategoryList = products.get(index).getClassifiedTaxCategory();
		final TaxCategoryType newCategory = InstanceFactory.getTaxCategory(value);
		TaxCategoryType oldCategory = null;
		if(!taxCategoryList.isEmpty())
			oldCategory = taxCategoryList.get(0);
		if(hasCellValueChanged(oldCategory, newCategory))
		{
			taxCategoryList.clear();
			taxCategoryList.add(newCategory);
		}
	}

	/**
	 * Retrieves the list of keywords set for the {@link Item}.
	 * @param index index of the {@code Item} in the list of products
	 * @return list of keywords
	 */
	public String getProductKeywordsAsString(final int index)
	{
		final Item item = products.get(index);
		return item.getKeywordCount() == 0 ? null :
			item.getKeyword().stream().map(keyword -> keyword.getValue()).collect(Collectors.joining(", "));
	}

	public List<KeywordType> getProductKeywords(final int index)
	{
		final Item item = products.get(index);
		return item.getKeyword();
	}

	public void setProductKeywords(int index, List<KeywordType> value)
	{
		final Item item = products.get(index);
		item.setKeyword(value);
	}

	public void setProductKeywords(int index, String value)
	{
		value = value.trim();
		final Item item = products.get(index);
		List<KeywordType> keywords =
				Stream.of(value.split("( )*[,;]+")).map(keyword -> new KeywordType(keyword)).collect(Collectors.toList());
		item.setKeyword(keywords);
	}

	/**
	 * Gets the next eligable ID for the {@link Item product}.
	 * <p> Method is not optimized.
	 * @return product's ID
	 */
	private String getNextProductID()
	{
		String nextID = String.valueOf(++itemID);
		while(!isUniqueProductID(nextID))
			nextID = String.valueOf(++itemID);
		return nextID;
	}

	/**
	 * Tests whether the ID is unique in the list of all {@code Item product}s.
	 * @param id ID to test
	 * @return true if it is unique, false otherwise
	 */
	private boolean isUniqueProductID(String id)
	{
		try
		{
			return !products.stream().anyMatch(item -> id.equals(item.getSellersItemIdentification().getIDValue()));
		}
		catch(Exception e) // if for some reason ID could not be accessed e.g. when it is not set - should not happen but still
		{
			return true;
		}
	}

	/**
	 * Returns the number of My Party's products.
	 * @return number of products
	 */
	public int getProductCount()
	{
		return products.size();
	}

	/**
	 * Returns ID of the latest created Catalogue Document.
	 * @return catalogue ID
	 */
	public long getCatalogueID()
	{
		return catalogueID;
	}

	public void setCatalogueID(long catalogueID)
	{
		this.catalogueID = catalogueID;
	}

	/**
	 * Returns next ID for the newly created Catalogue Document.
	 * @return next catalogue ID
	 */
	public long nextCatalogueID()
	{
		return ++catalogueID;
	}

	/**
	 * Returns ID of the latest created Catalogue Deletion Document.
	 * @return catalogue deletion ID
	 */
	public long getCatalogueDeletionID()
	{
		return catalogueDeletionID;
	}

	public void setCatalogueDeletionID(long catalogueDeletionID)
	{
		this.catalogueDeletionID = catalogueDeletionID;
	}

	/**
	 * Returns next ID for the newly created CatalogueDeletion Document.
	 * @return next catalogue deletion ID
	 */
	public long nextCatalogueDeletionID()
	{
		return ++catalogueDeletionID;
	}

	/**
	 * Returns ID of the latest created {@link Item product item}.
	 * @return item ID
	 */
	public long getItemID()
	{
		return itemID;
	}

	public void setItemID(long itemID)
	{
		this.itemID = itemID;
	}

	/**
	 * Gets issue date of the latest catalogue.
	 * @return catalogue issue date
	 */
	public XMLGregorianCalendar getCatalogueIssueDate()
	{
		return catalogueIssueDate;
	}

	/**
	 * Sets catalogue issue date to current date.
	 * @return set catalogue issue date
	 */
	public XMLGregorianCalendar setCatalogueIssueDate()
	{
		this.catalogueIssueDate = InstanceFactory.getDate();
		return catalogueIssueDate;
	}

	/**
	 * Sets Catalogue issue date to {@code null}. Usually this is done after the Catalogue has been
	 * deleted from the CDR.
	 */
	public void removeCatalogueIssueDate()
	{
		this.catalogueIssueDate = null;
	}

	/**
	 * Tells whether Catalogue has ever been sent to the CDR.
	 * @return true is CDR has My Party's Catalogue, false otherwise
	 */
	public boolean isCatalogueInCDR()
	{
		return catalogueIssueDate != null ? true : false;
	}

	/**
	 * Removes all parties from the data model and data store.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @throws DetailException if data could not be deleted from the database
	 */
	public void clearParties() throws DetailException
	{
		MapperRegistry.getInstance().getMapper(BusinessParty.class).deleteAll();
		notifyListeners(new BusinessPartyEvent(new ArrayList<>(), BusinessPartyEvent.ALL_PARTIES_REMOVED));
		setArchivedParties(null);
		setBusinessPartners(null);
		setDeregisteredParties(null);
		setOtherParties(null);
	}

	/**
	 * Removes all searches from the data model and data store.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @throws DetailException if data could not be deleted from the database
	 */
	public void clearSearches() throws DetailException
	{
		//MMM:		TODO notifyListeners(new BusinessPartyEvent(partySearches, SearchEvent.ALL_PARTY_SEARCHES_REMOVED));
		MapperRegistry.getInstance().getMapper(PartySearch.class).deleteAll();
		setPartySearches(null);
		MapperRegistry.getInstance().getMapper(CatalogueSearch.class).deleteAll();
		setCatalogueSearches(null);
	}

	/**
	 * Gets the copy of MyParty previously retrieved from the CDR service.
	 * @return MyParty object or {@code null}
	 */
	public BusinessParty getMyFollowingParty()
	{
		return myFollowingParty;
	}

	/**
	 * Sets MyParty data previously retrvied from the CDR service.
	 * @param myFollowingParty
	 */
	public void setMyFollowingParty(BusinessParty myFollowingParty)
	{
		this.myFollowingParty = myFollowingParty;
	}

	/**
	 * Gets the {@code List} of all parties that are bussines partners of MyParty.
	 * @return list of business partners
	 */
	public List<BusinessParty> getBusinessPartners()
	{
		if(businessPartners == null)
			businessPartners = new ArrayList<BusinessParty>();
		//MMM: next line will be superfluos
//		businessPartners = getFollowingParties().stream().filter(party -> party.isPartner()).collect(Collectors.toList());
		return businessPartners;
	}

	/**
	 * Sets the list of business partners.
	 * @param businessPartners list of business partners to set
	 */
	public void setBusinessPartners(@Nullable List<BusinessParty> businessPartners)
	{
		this.businessPartners = businessPartners;
	}

	/**
	 * Removes all parties from the business partners list.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @throws DetailException if data could not be deleted from the data store
	 */
	public void clearBusinessPartners() throws DetailException
	{
		for(BusinessParty party : getBusinessPartners())
			MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
		notifyListeners(new BusinessPartyEvent(businessPartners, BusinessPartyEvent.BUSINESS_LIST_REMOVED));
		setBusinessPartners(null);
	}

	/**
	 * Adds party to business partner list.
	 * @param party party to add
	 */
	private void addBusinessPartner(BusinessParty party)
	{
		if(party != null)
		{
			party.setPartner(true);
			getBusinessPartners().add(party);
		}
	}

	/**
	 * Removes party from the list of business partners.
	 * @param party party to remove
	 * @return true if party was contained in list of business parties and removed from it
	 */
	private boolean removeBusinessPartner(BusinessParty party)
	{
		boolean success = false;
		if(party != null)
		{
			success = getBusinessPartners().remove(party);
			party.setPartner(false);
		}
		return success;
	}

	/**
	 * Gets the {@code List} of all parties that are not bussines partners of MyParty. Those parties are just
	 * followed by MyParty.
	 * @return list of followed parties that are not business partners
	 */
	public List<BusinessParty> getOtherParties()
	{
		if(otherParties == null)
			otherParties = new ArrayList<BusinessParty>();
		//MMM: next line will be superfluos
//		otherParties = getFollowingParties().stream().filter(party -> ! party.isPartner()).collect(Collectors.toList());
		return otherParties;
	}

	/**
	 * Sets the list of parties that are followed but not are business partners.
	 * @param otherParties list of other parties to set
	 */
	public void setOtherParties(@Nullable List<BusinessParty> otherParties)
	{
		this.otherParties = otherParties;
	}

	/**
	 * Removes all parties from the other parties list.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @throws DetailException if data could not be deleted from the data store
	 */
	public void clearOtherParties() throws DetailException
	{
		for(BusinessParty party : getOtherParties())
			MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
		notifyListeners(new BusinessPartyEvent(otherParties, BusinessPartyEvent.OTHER_LIST_REMOVED));
		setOtherParties(null);
	}

	/**
	 * Adds party to other parties list.
	 * @param party party to add
	 */
	private void addOtherParty(BusinessParty party)
	{
		if(party != null)
		{
			party.setPartner(false);
			getOtherParties().add(party);
		}
	}

	/**
	 * Removes party from the list of business partners.
	 * @param party party to remove
	 *  @return true if party was contained in list of other parties and removed from it
	 */
	private boolean removeOtherParty(BusinessParty party)
	{
		boolean success = false;
		if(party != null)
		{
			party.setPartner(false);
			success = getOtherParties().remove(party);
		}
		return success;
	}

	public List<BusinessParty> getArchivedParties()
	{
		if(archivedParties == null)
			archivedParties = new ArrayList<>();
		return archivedParties;
	}

	public void setArchivedParties(List<BusinessParty> archivedParties)
	{
		this.archivedParties = archivedParties;
	}

	/**
	 * Removes all parties from the archived parties list.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @throws DetailException if data could not be deleted from the data store
	 */
	public void clearArchivedParties() throws DetailException
	{
		for(BusinessParty party : getArchivedParties())
			MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
		notifyListeners(new BusinessPartyEvent(archivedParties, BusinessPartyEvent.ARCHIVED_LIST_REMOVED));
		setArchivedParties(null);
	}

	/**
	 * Gets archived party with passed Party ID.
	 * @param id archived party's ID
	 * @return archived party or {@code null} if there is no party with specified ID in the list of
	 * archived parties
	 */
	public BusinessParty getArchivedParty(String id)
	{
		try
		{
			return getArchivedParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
		}
		catch(NoSuchElementException e) //if there is no party with passed id
		{
			return null;
		}
	}

	/**
	 * Checks whether the Party is archived. Check is based on the Party ID extracted from the passed {@code party} argument.
	 * So, different Party objects with the same Party ID will have the same result.
	 * @param party party in check
	 * @return true if Party is archived, false otherwise
	 */
	private boolean checkArchivedParty(BusinessParty party)
	{
		String partyID = party.getPartyID();
		return getArchivedParty(partyID) != null ? true : false;
	}

	/**
	 * Adds party to archived party list.
	 * @param party party to add
	 */
	private void addArchivedParty(BusinessParty party)
	{
		if(party != null)
		{
			party.setArchived(true);
			getArchivedParties().add(party);
		}
	}

	/**
	 * Removes party from the list of archived parties.
	 * @param party party to remove
	 * @return true if party was contained in list of archived parties and removed from it
	 */
	private boolean removeArchivedParty(BusinessParty party)
	{
		boolean success = false;
		if(party != null)
		{
			//MMM: TODO can be removed only if there are no correspondence/documents with the party
			party.setArchived(false);
			success = getArchivedParties().remove(party);
		}
		return success;
	}

	/**
	 * Adds party to the archived list.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @param party party be archive
	 */
	public void archiveParty(BusinessParty party)
	{
		if(party != null)
		{
			addArchivedParty(party);
			notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.ARCHIVED_PARTY_ADDED));
		}
	}

	/**
	 * Deletes party from the archived or deregistered list.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @param party party be deleted
	 */
	public void purgeParty(BusinessParty party)
	{
		if(party != null)
		{
			if(party.isArchived())
			{
				removeArchivedParty(party);
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.ARCHIVED_PARTY_REMOVED));
			}
			else if(party.isDeregistered())
			{
				removeDeregisteredParty(party);
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.DEREGISTERED_PARTY_REMOVED));
			}
		}
	}

	public List<BusinessParty> getDeregisteredParties()
	{
		if(deregisteredParties == null)
			deregisteredParties = new ArrayList<>();
		return deregisteredParties;
	}

	/**
	 * Removes all parties from the deregistered parties list.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @throws DetailException if data could not be deleted from the data store
	 */
	public void clearDeregisteredParties() throws DetailException
	{
		for(BusinessParty party : getDeregisteredParties())
			MapperRegistry.getInstance().getMapper(BusinessParty.class).delete(null, party.getPartyID());
		notifyListeners(new BusinessPartyEvent(deregisteredParties, BusinessPartyEvent.DEREGISTERED_LIST_REMOVED));
		setDeregisteredParties(null);
	}

	public void setDeregisteredParties(List<BusinessParty> deregisteredParties)
	{
		this.deregisteredParties = deregisteredParties;
	}

	/**
	 * Gets the deregistered party with passed Party ID.
	 * @param id deregistered party's ID
	 * @return Deregistered party or {@code null} if there is no party with specified ID in the list of
	 * Deregistered parties
	 */
	public BusinessParty getDeregisteredParty(String id)
	{
		try
		{
			return getDeregisteredParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
		}
		catch(NoSuchElementException e) //if there is no party with passed id
		{
			return null;
		}
	}

	/**
	 * Checks if the Party is Deregistered. Check is based on the Party ID only extracted from passed Party argument.
	 * So different Party objects with the same Party ID will have the same result.
	 * @param party party in check
	 * @return true if Party is Deregistered, false otherwise
	 */
	private boolean checkDeregisteredParty(BusinessParty party)
	{
		String partyID = party.getPartyID();
		return getDeregisteredParty(partyID) != null ? true : false;
	}

	/**
	 * Adds to deregisterd party list and removes it from archived list if it is in it.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @param party party to add
	 */
	private void addDeregisteredParty(BusinessParty party)
	{
		if(party != null)
		{
			party.setDeregistered(true);
			getDeregisteredParties().add(party);
			notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.DEREGISTERED_PARTY_ADDED));

			if(checkArchivedParty(party)) //MMM: superfluos??? remove down below will succeed or not
			{	//This way it is removed the object with the same Party ID, not just
				//the same object like in this call: getArchivedParties().remove(party)
				if(getArchivedParties().remove(getArchivedParty(party.getPartyID())))
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.ARCHIVED_PARTY_REMOVED));
			}
		}
	}

	/**
	 * Removes party from the list of Deregistered parties.
	 * @param party party to be removed
	 * @return true if party was contained in list of deregistered parties and removed from it
	 */
	private boolean removeDeregisteredParty(BusinessParty party)
	{
		boolean success = false;
		if(party != null)
		{
			//MMM: TODO should be removed only if there are no correspondence/documents with the party
			success = getDeregisteredParties().remove(party);
			party.setDeregistered(false);
		}
		return success;
	}

	/**
	 * Adds party to the deregistered list and removes it from the lists it is contained in.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @param party party be deregister
	 */
	public void deregisterParty(BusinessParty party)
	{
		if(party != null)
		{
			removeFollowingParty(party);
			addDeregisteredParty(party);
			notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.DEREGISTERED_PARTY_ADDED));
		}
	}

/*	*//**
	 * Deletes party from the deregistered list.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @param party party be deleted
	 *//*
	 *@Deprecated
	public void deleteDeregisteredParty(BusinessParty party)
	{
		if(party != null)
		{
			removeDeregisteredParty(party);
			notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.DEREGISTERED_PARTY_REMOVED));
		}
	}*/

	/**
	 * Gets the {@code List} of all parties that are followed by MyParty.
	 * @return list of all followed parties
	 */
	public List<BusinessParty> getFollowingParties()
	{
		/*if (followingParties == null)
			followingParties = new ArrayList<>();
		return followingParties;*/
		setFollowingParties(); //not optimized because this merges two lists every time method is invoked
		return followingParties;
	}

	public void setFollowingParties(List<BusinessParty> followingParties)
	{
		this.followingParties = followingParties;
		separateFollowingParties(followingParties);
	}

	/**
	 * Populate the list of following parties by mearging the lists of business partner and
	 * other parties.
	 */
	public void setFollowingParties()
	{
		followingParties = new ArrayList<>();
		followingParties.addAll(getBusinessPartners());
		followingParties.addAll(getOtherParties());
	}

	/**
	 * Populates helper lists of {@link #businessPartners} and {@link #otherParties} from the
	 * list of all {@link #followingParties}.
	 * @param followingParties list of all {@link #followingParties}
	 */
	private void separateFollowingParties(List<BusinessParty> followingParties)
	{
		if(followingParties == null)
			businessPartners = otherParties = null;
		else
		{
			businessPartners = getBusinessPartners();
			otherParties = getOtherParties();
		}

/*		for(BusinessParty bp : followingParties)
		{
			if(bp.isPartner())
				businessPartners.add(bp);
			else
				otherParties.add(bp);
		}*/
	}

	/**
	 * Gets the following party with passed ID.
	 * @param id following party's ID
	 * @return following party or {@code null} if there is no party with specified ID in the
	 * list of following parties
	 */
	public BusinessParty getFollowingParty(String id)
	{
		try
		{
			return getFollowingParties().stream().filter(party -> id.equals(party.getCoreParty().getPartyID())).findFirst().get();
		}
		catch(NoSuchElementException e) //if there is no party with passed id
		{
			return null;
		}
	}

	/**
	 * Adds party to the list of following parties and to one appropriate helper list while removing it
	 * from the other. If party is present in the list, this method overwrites it.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @param party following party
	 */
	@Deprecated
	private void addFollowingParty(BusinessParty party)
	{
		if(party != null)
		{
			//removal is necessary when party is already in the list, but its partner status has been changed
			//so this method is called because Party should be moved to the other helper list. Cumbersome.
			getFollowingParties().remove(party); //if exists MMM: better to use Sets then?
			getFollowingParties().add(party);

			if(checkArchivedParty(party))// MMM:superflous???
			{	//if following party is archived sometime before, object representing that party andreferenced
				//in the archived list has the same Party ID
				//but is not the same object as the one passed to this method. There should be removed the
				//object with the same Party ID, not the same object like it would be done by this method call:
				//getArchivedParties().remove(party). That's way this is invoked:

				/*if(getArchivedParties().remove(getArchivedParty(party.getPartyID())))
					notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.ARCHIVED_PARTY_REMOVED));*/

				//Not neccessary to separetly notify about deletion from the archived list; archived tree will be
				//updated by notification below
				getArchivedParties().remove(getArchivedParty(party.getPartyID()));

			}
			if(party.isPartner())
			{
				getBusinessPartners().add(party);
				getOtherParties().remove(party);
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.BUSINESS_PARTNER_ADDED));
			}
			else
			{
				getBusinessPartners().remove(party);
				getOtherParties().add(party);
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.OTHER_PARTY_ADDED));
			}
		}
	}

	/**
	 * Adds party to the list of following parties, and sets it as a business partner in respect of
	 * {@code partner} argument.
	 * @param party following party to add
	 * @param partner whether the party is a business partner
	 * @return {@code BusinessParty} with its {@code coreParty} field asigned with passed {@code party} argument
	 * or {@code null} if {@code party} argument has a {@code null} value
	 */
	@Deprecated
	private BusinessParty addFollowingParty(PartyType party, boolean partner)
	{
		BusinessParty newParty = null;
		if(party != null)
		{
			newParty = new BusinessParty();
			newParty.setCoreParty(party);
			newParty.setPartner(partner);
			addFollowingParty(newParty);
		}
		return newParty;
	}

	/**
	 * Removes party from proper list of following parties.
	 * @param party party to remove
	 * @return true if party was contained in some of the following lists and removed from it
	 */
	private boolean removeFollowingParty(BusinessParty party)
	{
		boolean success = false;
		if(party != null)
		{
			if(party.isPartner())
				success = removeBusinessPartner(party);
			else
				success = removeOtherParty(party);
			party.setFollowing(false);
		}
		return success;
	}

	/**
	 * Checks whether the Party is followed. Check is based on the Party ID only extracted from passed
	 * Party argument. So, different Party objects with the same Party ID will have the same result.
	 * @param party party in check
	 * @return true if Party is followed, false otherwise
	 */
	private boolean checkFollowingParty(BusinessParty party)
	{
		String partyID = party.getPartyID();
		return getFollowingParty(partyID) != null ? true : false;
	}

	/**
	 * Adds party to the proper list of {@link BusinessParty BusinessParties} and removes it from the
	 * archived list if the party was archived before.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 * @param party party to follow
	 */
	public void followParty(BusinessParty party)
	{
		party.setFollowing(true);

		//if following party is archived sometime before, object representing that party and referenced
		//in the archived list has the same Party ID
		//but is not the same object as the one passed to this method. There should be removed the
		//object with the same Party ID, not the same object like it would be done by this method call:
		//getArchivedParties().remove(party). That's way this is invoked:
		final BusinessParty archivedParty = getArchivedParty(party.getPartyID());
		boolean archived = removeArchivedParty(archivedParty);
		if(archived)
			notifyListeners(new BusinessPartyEvent(archivedParty, BusinessPartyEvent.ARCHIVED_PARTY_REMOVED));
//		getArchivedParties().remove(getArchivedParty(party.getPartyID()));

		boolean other = false, partner = false;
		if(party.isPartner())
		{
			other = removeOtherParty(party);
			addBusinessPartner(party);
			if(other)
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.BUSINESS_PARTNER_TRANSFERED));
			else
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.BUSINESS_PARTNER_ADDED));
		}
		else
		{
			partner = removeBusinessPartner(party);
			addOtherParty(party);
			if(partner)
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.OTHER_PARTY_TRANSFERED));
			else
				notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.OTHER_PARTY_ADDED));
		}
	}

	/**
	 * Unfollows the party by removing it from appropriate list and by sending the notification to tree model
	 * listener.
	 * @param party party to unfollow
	 */
	public void unfollowParty(BusinessParty party)
	{
		boolean partner = party.isPartner();
		removeFollowingParty(party);
		addArchivedParty(party);
		if(partner)
			notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.BUSINESS_PARTNER_REMOVED));
		else
			notifyListeners(new BusinessPartyEvent(party, BusinessPartyEvent.OTHER_PARTY_REMOVED));
	}

	public List<Search<PartyType>> getPartySearches()
	{
		if(partySearches == null)
			partySearches = new ArrayList<Search<PartyType>>();
		return partySearches;
	}

	public void setPartySearches(@Nullable List<Search<PartyType>> partySearches)
	{
		this.partySearches = partySearches;
	}

	public List<Search<CatalogueType>> getCatalogueSearches()
	{
		if(catalogueSearches == null)
			catalogueSearches = new ArrayList<Search<CatalogueType>>();
		return catalogueSearches;
	}

	public void setCatalogueSearches(@Nullable List<Search<CatalogueType>> catalogueSearches)
	{
		this.catalogueSearches = catalogueSearches;
	}

	/**
	 * Checks whether My Party is registered with the CDR service.
	 * @return true if party is registered with the CDR service, otherwise false
	 */
	public boolean isRegisteredWithCDR()
	{
		//return getCoreParty().getPartyID() == null ? false : true;
		return hasCDRSecretKey();
	}

	/**
	 * Checks whether My Party is registered with the local datastore.
	 * @return true if party is registered with the local datastore, otherwise false
	 */
	public boolean isRegisteredWithLocalDatastore()
	{
		return localUser != null && localUser.getSecretKey() != null;
	}

	/**
	 * Checks whether My Party's cdr User has a secret key.
	 * @return true if party has a secret key, otherwise false
	 */
	public boolean hasCDRSecretKey()
	{
		return cdrUser != null && cdrUser.getSecretKey() != null;
	}

	public boolean isDirtyCatalogue()
	{
		return dirtyCatalogue;
	}

	public void setDirtyCatalogue(Boolean dirty)
	{
		dirtyCatalogue = dirty;
	}

	//MMM: maybe this method is not necessary, because of the above one which might be mandatory because of the JAXB serialization ???
	public void setDirtyCatalogue(boolean dirtyCatalogue)
	{
		this.dirtyCatalogue = dirtyCatalogue;
	}

	public RutaUser getLocalUser()
	{
		return localUser;
	}

	public void setLocalUser(RutaUser localUser)
	{
		this.localUser = localUser;
	}

	public RutaUser getCDRUser()
	{
		return cdrUser;
	}

	public void setCDRUser(RutaUser cdrUser)
	{
		this.cdrUser = cdrUser;
	}

	/**
	 * Generates {@link CatalogueType} document from {@link Item items} in the Product table. Method returns
	 * {@code null} if Catalogue does not have any item or some item has no name or is empty string.
	 * @param receiverParty receiver Party of the Catalogue
	 * @return catalogue or {@code null}
	 */
	public CatalogueType createCatalogue(Party receiverParty)
	{
		CatalogueType catalogue = null;
		int lineCnt = 0;
		final List<Item> myProducts = getProducts();
		if(checkProductNames(myProducts) && !myProducts.isEmpty())
		{
			//populating Catalogue document
			catalogue = new CatalogueType();
			final IDType catID = new IDType();
			final String strID = String.valueOf(nextCatalogueID());
			catID.setValue(strID);
			catalogue.setID(catID);
			final UUIDType catUUID = new UUIDType();
			catUUID.setValue(getCatalogueUUID());
			catalogue.setUUID(catUUID);
			final IssueDateType date = new IssueDateType();
			date.setValue(setCatalogueIssueDate());
			catalogue.setIssueDate(date);
			catalogue.setProviderParty((PartyType) getCoreParty());
			catalogue.setReceiverParty((PartyType) receiverParty);

			for(Item prod : myProducts)
			{
				final ItemType item = (ItemType) prod.clone();// using only ItemType part of the Item ???
				final CatalogueLineType catLine = new CatalogueLineType();
				final IDType catLineID = new IDType();
				catLineID.setValue(catID.getValue() + "-" + lineCnt++);
				catLine.setID(catLineID);
				catLine.setItem(item);
				List<ItemLocationQuantityType> itemLocationList = catLine.getRequiredItemLocationQuantity();
				ItemLocationQuantityType itemLocation = new ItemLocationQuantityType();
				itemLocation.setPrice(prod.getPrice());
				itemLocationList.add(itemLocation);
				catalogue.addCatalogueLine(catLine);
			}
		}
		return catalogue;
	}

	/**
	 * Generates {@link CatalogueDeletionType Catalogue Deletion Document}.
	 * @return {@code CatalogueDeletionType Catalogue Deletion Document}.
	 */
	public CatalogueDeletionType createCatalogueDeletion(Party CDRParty)
	{
		CatalogueDeletionType catalogueDeletion = new CatalogueDeletionType();
		String strID = String.valueOf(nextCatalogueDeletionID());
		IDType catDelID = new IDType(strID);
		catalogueDeletion.setID(catDelID);
		UUIDType catDelUUID = new UUIDType(getCatalogueDeletionUUID());
		catalogueDeletion.setUUID(catDelUUID);
		catalogueDeletion.setIssueDate(InstanceFactory.getDate());
		catalogueDeletion.setDeletedCatalogueReference(getCatalogueReference());
		catalogueDeletion.setProviderParty((PartyType) getCoreParty());
		catalogueDeletion.setReceiverParty((PartyType) CDRParty);
		return catalogueDeletion;
	}

	/**
	 * Creates object of {@code CatalogueReferenceType} representing reference to the latest
	 * created {@code CatalogueType} document.
	 * @return object representing reference to the lateset catalogue document
	 */
	private CatalogueReferenceType getCatalogueReference()
	{
		CatalogueReferenceType catRef = new CatalogueReferenceType();
		catRef.setID(String.valueOf(getCatalogueID()));
		catRef.setUUID(getCatalogueUUID());
		catRef.setIssueDate(getCatalogueIssueDate());
		return catRef;
	}

	/**
	 * Gets String representing UUID of the lastly created Catalogue Document.
	 * @return UUID of lastly created Catalogue as String
	 */
	private String getCatalogueUUID()
	{
		return getCoreParty().getPartyID()+ "CAT" + getCatalogueID();
	}

	/**
	 * Gets String representing UUID of the lastly created {@link CatalogueDeletionType} document.
	 * @return UUID of lastly created {@code CatalogueDeletion} as String
	 */
	private String getCatalogueDeletionUUID()
	{
		return getCoreParty().getPartyID()+ "CDL" + getCatalogueDeletionID();
	}

	/**
	 * Checks whether all {@link Item items} in the list have a name that differs from an empty string.
	 * @param myProducts list of items
	 * @return true if all item have a name, false otherwise
	 */
	private boolean checkProductNames(List<Item> myProducts)
	{
		boolean ok = true;
		for(Item prod : myProducts)
		{
			String productName = prod.getNameValue();
			if (productName == null || productName.equals(""))
			{
				ok = false;
				break;
			}
		}
		return ok;
	}

	/**
	 * Checks whether the cell value has changed. The value is considered not being changed
	 * if a new value is equal to an empty string and old value is a {@code null}. If it has
	 * changed {@code dirtyCatalogue} field is set to true.
	 * <p>This method <b>must</b> be called during every change of any cell of the {@code Product Table},
	 * because it is updating {@code dirtyCatalogue} flag.
	 * @param oldOne old value of the cell
	 * @param newOne new value of the cell
	 * @return true if the values differs, false otherwise
	 */
	private <T> boolean hasCellValueChanged(T oldOne, T newOne)
	{
		boolean changed = false;
		if(newOne != null)
		{
			if(newOne instanceof String && newOne.toString().equals("") && oldOne == null)
				changed = false;
			changed = !newOne.equals(oldOne);
		}
		dirtyCatalogue = dirtyCatalogue || changed;
		return changed;
	}

	public String getCDRPassword()
	{
		return cdrUser.getPassword();
	}

	public void setCDRPassword(String password)
	{
		cdrUser.setPassword(password);
	}

	public String getCDRUsername()
	{
		return cdrUser.getUsername();
	}

	public void setCDRUsername(String username)
	{
		cdrUser.setUsername(username);
	}

	public String getLocalPassword()
	{
		return localUser.getPassword();
	}

	public void setLocalPassword(String password)
	{
		localUser.setPassword(password);
	}

	public String getLocalUsername()
	{
		return localUser.getUsername();
	}

	public void setLocalUsername(String username)
	{
		localUser.setUsername(username);
	}

	/**
	 * Gets the boolean value telling if the {@code Party} data are in sync with the CDR.
	 * @return true if the {@code Party} is in sync with the CDR
	 */
	public boolean isDirtyMyParty()
	{
		return dirtyMyParty;
	}

	/**
	 * Sets the value of the boolean flag designating whether the {@code Party} object has been changed
	 * since it is retrieved from the database or updated with the CDR service. If the value is true
	 * that means the the local copy of the {@code Party} is out of sync with the CDR.
	 * @param dirtyMyParty boolean value to be set
	 */
	public void setDirtyMyParty(boolean dirtyMyParty)
	{
		this.dirtyMyParty = dirtyMyParty;
	}

	/**
	 * Returns secret key of the remote {@link User} used for the SOAP message digest.
	 * @return secret key or {@code null} if it is not defined
	 */
	public String getCDRSecretKey()
	{
		return "".equals(cdrUser.getSecretKey()) ? null : cdrUser.getSecretKey();
	}

	/**
	 * Sets the value of the Party's remote {@link User} secret key used for the SOAP message digest encryption and decryption.
	 * @param secretKey secret key to set
	 */
	public void setCDRSecretKey(String secretKey)
	{
		cdrUser.setSecretKey(secretKey);
	}

	/**
	 * Returns secret key of the local {@link User} used for the local data store connection.
	 * @return secret key or {@code null} if it is not defined
	 */
	public String getLocalSecretKey()
	{
		return "".equals(localUser.getSecretKey()) ? null : localUser.getSecretKey();
	}

	/**
	 * Sets the value of the local {@link User}'s secret key used for the local data store connection.
	 * @param secretKey secret key to set
	 */
	public void setLocalSecretKey(String secretKey)
	{
		localUser.setSecretKey(secretKey);
	}

	/**
	 * Gets the boolean value that tells whether the Catalogue should be inserted in or updated with the CDR service.
	 * @return true if Catalogue should be deposited in the CDR service for the first time i.e inserted,
	 * false otherwise i.e. updated.
	 */
	public boolean isInsertMyCatalogue()
	{
		return insertMyCatalogue;
	}

	/**
	 * Sets the value of the field that tells whether the Catalogue should be inserted in or updated with the CDR service.
	 * @param insertMyCatalogue true if Catalogue should be deposited in the CDR service for the first time i.e. inserted,
	 * false otherwise i.e. updated.
	 */
	public void setInsertMyCatalogue(boolean insertMyCatalogue)
	{
		this.insertMyCatalogue = insertMyCatalogue;
	}

	public long getSearchNumber()
	{
		return searchNumber;
	}

	public void setSearchNumber(long searchNumber)
	{
		this.searchNumber = searchNumber;
	}

	/**
	 * Register My Party as a following party of a self.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 */
	public void followMyself()
	{
		myFollowingParty = new BusinessParty();
		//copying name value because it is necessary for the tree node display, because
		//the node has to be displayed before My Party data is retrieved from the CDR
		//and the name string is used for the node display
		myFollowingParty.setPartySimpleName(getPartySimpleName());
		//copying Party ID value because it is necessary for the database insert, which
		//could be done before My Party is retrieved from the CDR
		myFollowingParty.setPartyID(getPartyID());
		notifyListeners(new BusinessPartyEvent(myFollowingParty, BusinessPartyEvent.MY_PARTY_ADDED));
	}

	/**
	 * Deregister My Party as a following party of a self.
	 * <p>Notifies listeners registered for this type of {@link BusinessPartyEvent event}.</p>
	 */
	public void unfollowMyself()
	{
		notifyListeners(new BusinessPartyEvent(myFollowingParty, BusinessPartyEvent.MY_PARTY_REMOVED));
		myFollowingParty = null;
	}

	/**
	 * Removes {@link Item} from the product list and from the database.
	 * @param row product's index
	 * @throws Exception if {@code Item} could not be deleted from the database
	 */
	public void removeProduct(final int row) throws Exception
	{
		final String id = products.get(row).getID().getValue();
		MapperRegistry.getInstance().getMapper(Item.class).delete(null, id);
		products.remove(row);
		dirtyCatalogue = true;
	}

	/**
	 * Adds passed {@link Item} to the product list and inserts it in the database.
	 * @param item {@link Item} to be added
	 * @throws DetailException if {@code Item item} could not be inserted in the database
	 */
	public void addProduct(final Item item) throws DetailException
	{
		MapperRegistry.getInstance().getMapper(Item.class).insert(null, item);
		products.add(item);
		dirtyCatalogue = true;
	}

	/**
	 * Adds new empty {@link Item} to the product list. Sets unique product ID and
	 * unique {@code Item}'s ID used for its database identification.
	 * @param index {@code Item item}'s index in the list
	 * @throws DetailException if product could not be inserted in the database
	 */
	public void addNewEmptyProduct(final int index) throws DetailException
	{
		final Item item = new Item();
		item.setID(UUID.randomUUID().toString());
		addProduct(item);
		try
		{
			setProductID(index, getNextProductID());
		}
		catch (ProductException e) //should not happen
		{
			logger.error("Product ID si not eligable", e);
		}
	}

	//MMM: because of view update, shouldn't this method be placed in Client class???
	void placeDocBoxCatalogue(CatalogueType catalogue, String docID)
	{
		PartyType provider = catalogue.getProviderParty();
		if(sameParties(this, provider))
		{
			getMyFollowingParty().setCatalogue(catalogue);
			//MMM: update the view (tree view, table....) ; bolding the name of the node in tree view
		}
		else
		{
			for(BusinessParty bParty: getFollowingParties())
				if(sameParties(bParty, provider))
				{
					bParty.setCatalogue(catalogue);
					//MMM: update the view (tree view, table....) ; bolding the name of the node in tree view
					break;
				}
		}
	}

	public void placeDocBoxParty(PartyType party, String docID)
	{
		if(sameParties(this, party))
			getMyFollowingParty().setCoreParty(party);
		else
			for(BusinessParty bParty: getFollowingParties())
			{
				if(sameParties(bParty, party))
				{
					bParty.setCoreParty(party);
					break;
				}
			}
	}

	public void placeDocBoxCatalogueDeletion(CatalogueDeletionType catDeletion, String docID)
	{
		PartyType provider = catDeletion.getProviderParty();
		if(sameParties(this, provider))
		{
			getMyFollowingParty().setCatalogue(null);
		}
		else
			for(BusinessParty bParty: getFollowingParties())
			{
				if(sameParties(bParty, provider))
				{
					bParty.setCatalogue(null);
					break;
				}
			}
	}

	public void placeDocBoxDeregistrationNotice(DeregistrationNotice notice, String docID)
	{
		PartyType party = notice.getParty();
		for(BusinessParty bParty: getFollowingParties())
			if(sameParties(bParty, party))
			{
				deregisterParty(bParty);
				break;
			}
	}

	/**
	 * Clears all data of My Party that are related to the CDR. This method is usually called
	 * during deregistration from the CDR service.
	 * @throws DetailException if data could not be deleted from the data store
	 */
	public void clearCDRRelatedData() throws DetailException
	{
		setDirtyMyParty(true);
		setDirtyCatalogue(true);
		setInsertMyCatalogue(true);
		setCDRSecretKey(null);
		setCDRUsername(null);
		setCDRPassword(null);
		getCoreParty().setPartyID(null);
		setCatalogueID(0);
		setCatalogueDeletionID(0);
		removeCatalogueIssueDate();
		unfollowMyself();
		clearParties();
	}

	/**
	 * @param search
	 */
	private void addPartySearch(final PartySearch search)
	{
		getPartySearches().add(0, search);
	}

	/**
	 * @param search
	 */
	private void removePartySearch(final PartySearch search)
	{
		getPartySearches().remove(search);
	}

	/**
	 * Adds {@link PartySearch} to the collection of all party searches.
	 * <p>Notifies listeners registered for this type of {@link SearchEvent event}.</p>
	 * @param search
	 */
	public void listPartySearch(final PartySearch search)
	{
		addPartySearch(search);
		notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_ADDED));
	}

	/**
	 * Removes {@link PartySearch} from the collection of all party searches.
	 * <p>Notifies listeners registered for this type of {@link SearchEvent event}.</p>
	 * @param search
	 */
	public void delistPartySearch(final PartySearch search)
	{
		removePartySearch(search);
		notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_REMOVED));
	}

	/**
	 * Updates {@link PartySearch} in the collection of all party searches.
	 * <p>Notifies listeners registered for this type of {@link SearchEvent event}.</p>
	 * @param search
	 */
	public void relistPartySearch(final PartySearch search)
	{
		removePartySearch(search);
		addPartySearch(search);
		notifyListeners(new SearchEvent(search, SearchEvent.PARTY_SEARCH_CHANGED));
	}

	/**
	 * Adds {@link PartySearch} to the collection of all party searches.
	 * @param search
	 */
	private void addCatalogueSearch(final Search<CatalogueType> search)
	{
		getCatalogueSearches().add(0, search);
	}

	/**
	 * Removes {@link PartySearch} from the collection of all party searches.
	 * @param search
	 */
	private void removeCatalogueSearch(final Search<CatalogueType> search)
	{
		getCatalogueSearches().remove(search);
	}

	/**
	 * Adds {@link CatalogueSearch} to the collection of all party searches.
	 * <p>Notifies listeners registered for this type of {@link SearchEvent event}.</p>
	 * @param search
	 */
	public void listCatalogueSearch(final Search<CatalogueType> search)
	{
		addCatalogueSearch(search);
		notifyListeners(new SearchEvent(search, SearchEvent.CATALOGUE_SEARCH_ADDED));
	}

	/**
	 * Removes {@link CatalogueSearch} from the collection of all party searches.
	 * <p>Notifies listeners registered for this type of {@link SearchEvent event}.</p>
	 * @param search
	 */
	public void delistCatalogueSearch(final Search<CatalogueType> search)
	{
		removeCatalogueSearch(search);
		notifyListeners(new SearchEvent(search, SearchEvent.CATALOGUE_SEARCH_REMOVED));
	}

	/**
	 * Updates {@link CatalogueSearch} in the collection of all party searches.
	 * <p>Notifies listeners registered for this type of {@link SearchEvent event}.</p>
	 * @param search
	 */
	public void relistCatalogueSearch(final Search<CatalogueType> search)
	{
		removeCatalogueSearch(search);
		addCatalogueSearch(search);
		notifyListeners(new SearchEvent(search, SearchEvent.CATALOGUE_SEARCH_CHANGED));
	}
}